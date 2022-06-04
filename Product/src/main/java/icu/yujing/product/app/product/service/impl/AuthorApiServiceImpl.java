package icu.yujing.product.app.product.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.UploadFileRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.constant.ProductModuleConstant;
import icu.yujing.common.constant.UserModuleConstant;
import icu.yujing.common.exception.MyTopException;
import icu.yujing.common.properties.AliOssProperties;
import icu.yujing.common.utils.Query;
import icu.yujing.common.utils.YujingUtils;
import icu.yujing.product.app.product.entity.po.VideoPo;
import icu.yujing.product.app.product.entity.vo.AuthorArticlesPageVo;
import icu.yujing.product.app.product.entity.vo.UploadArticleDetailsVo;
import icu.yujing.product.app.product.service.AuthorApiService;
import icu.yujing.product.app.product.service.VideoApiService;
import icu.yujing.product.app.product.service.ZoneApiService;
import icu.yujing.product.constant.VideoConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: Cyqurt
 * @date: 2022/3/27
 * @version: 1.0
 * @description:
 */
@Service
public class AuthorApiServiceImpl implements AuthorApiService {

    @Autowired
    private VideoApiService videoApiService;

    @Autowired
    private ZoneApiService zoneApiService;

    @Autowired
    private OSS ossClient;

    @Autowired
    private ThreadPoolExecutor threadPool;


    @Override
    public Page<VideoPo> getArticlesByAuthor(AuthorArticlesPageVo authorArticlesPageVo, Long userId) {
        // 固定 10个每页
        Page<VideoPo> page = new Page<VideoPo>(authorArticlesPageVo.getPage(), 10L);

        QueryWrapper<VideoPo> wrapper = new QueryWrapper<VideoPo>()
                .eq("user_id", userId);

        Query.order(authorArticlesPageVo.getOrderField(), authorArticlesPageVo.getOrderType(), Arrays.asList("uploading_date"), wrapper);

        if (authorArticlesPageVo.getStatus() != null) {
            wrapper.eq("status", authorArticlesPageVo.getStatus());
        }
        if (authorArticlesPageVo.getZoneId() != null) {
            wrapper.eq("zone_id", authorArticlesPageVo.getZoneId());
        }

        Page<VideoPo> pg = videoApiService.page(page, wrapper);
        List<VideoPo> records = pg.getRecords();

        // 获取所有视频的Id,用于从Redis中查询对应的视频播放量
        long[] videoIds = new long[records.size()];
        for (int i = 0; i < records.size(); i++) {
            videoIds[i] = records.get(i).getId();
        }

        // 从Redis中查询对对应视频的播放量
        Map<Long, Long> viewsMap = videoApiService.multiGetViewsOrLikes(videoIds, 0);
        for (VideoPo record : records) {
            record.setViews(viewsMap.get(record.getId()));
        }

        // 因为实时的播放量是放在Redis中的,所以不能再SQL中添加检索条件
        if ("views".equals(authorArticlesPageVo.getOrderField())) {
            if (authorArticlesPageVo.getOrderType() == 0) {
                records.sort((current, next) ->
                        (int) (next.getViews() - current.getViews())
                );
            } else {
                records.sort((current, next) ->
                        (int) (current.getViews() - next.getViews())
                );

            }
        }
        return pg;
    }


    @Override
    public String uploadArticleSlice(MultipartFile fileSlice, String uploadingId) throws IOException {
        String path = null;
        if (StringUtils.isEmpty(uploadingId)) {
            // 校验文件
            YujingUtils.checkFileTypeThrowException(fileSlice.getOriginalFilename(), VideoConstant.allowedVideoFileType);
            YujingUtils.checkFileSizeThrowException(fileSlice.getSize(), VideoConstant.allowedVideoSliceFileSize, YujingUtils.SizeUnit.MB);

            uploadingId = UUID.randomUUID().toString();
            path = YujingUtils.getPath("D:\\slice\\", fileSlice.getOriginalFilename());
            YujingUtils.createPath(path);
            VideoConstant.sliceUploadingMap.put(uploadingId, path);
        } else {
            path = VideoConstant.sliceUploadingMap.get(uploadingId);
            if (StringUtils.isEmpty(path)) {
                // 此uploadingId对应的数据已被定时任务删除
                throw new MyTopException(88888, "此次上传已过时");
            }
        }

        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        try {
            fos = new FileOutputStream(path, true);
            bis = new BufferedInputStream(fileSlice.getInputStream());
            byte[] buffer = new byte[5120];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
            }
        }
        return uploadingId;
    }

    @Override
    public String uploadArticleEnd(String uploadingId) {
        String localPath = VideoConstant.sliceUploadingMap.get(uploadingId);
        if (StringUtils.isEmpty(localPath)) {
            throw new MyTopException(88888, "文件已删除");
        }
        String aliOssPath = YujingUtils.getPath(UserModuleConstant.USER_ARTICLE_OSS_PREFIX, localPath);
        // 断点续传上传。
        CompletableFuture.runAsync(() -> {
            ObjectMetadata meta = new ObjectMetadata();
            // 指定上传的内容类型。
            meta.setContentType("text/plain");

            // 文件上传时设置访问权限ACL。
            // meta.setObjectAcl(CannedAccessControlList.Private);

            // 通过UploadFileRequest设置多个参数。
            // 依次填写Bucket名称（例如examplebucket）以及Object完整路径（例如exampledir/exampleobject.txt），Object完整路径中不能包含Bucket名称。
            UploadFileRequest uploadFileRequest = new UploadFileRequest(AliOssProperties.bucketName, aliOssPath);

            // 通过UploadFileRequest设置单个参数。
            // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
            uploadFileRequest.setUploadFile(localPath);
            // 指定上传并发线程数，默认值为1。
            uploadFileRequest.setTaskNum(1);
            // 指定上传的分片大小，单位为字节，取值范围为100 KB~5 GB。默认值为100 KB。
            uploadFileRequest.setPartSize(20 * 1024 * 1024);
            // 开启断点续传，默认关闭。
            uploadFileRequest.setEnableCheckpoint(true);
            // 记录本地分片上传结果的文件。上传过程中的进度信息会保存在该文件中，如果某一分片上传失败，再次上传时会根据文件中记录的点继续上传。上传完成后，该文件会被删除。
            // 如果未设置该值，默认与待上传的本地文件同路径，名称为${uploadFile}.ucp。
            // uploadFileRequest.setCheckpointFile("yourCheckpointFile");
            // 文件的元数据。
            uploadFileRequest.setObjectMetadata(meta);
            // 设置上传回调，参数为Callback类型。
            //uploadFileRequest.setCallback("yourCallbackEvent");

            try {
                ossClient.uploadFile(uploadFileRequest);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }, threadPool);
        return AliOssProperties.aliOssAddress + aliOssPath;
    }

    @Override
    public String uploadArticleCover(MultipartFile articleCover) throws IOException {

        String path = YujingUtils.getPath(UserModuleConstant.USER_ARTICLE_OSS_PREFIX, articleCover.getOriginalFilename());
        // 创建PutObjectRequest对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(AliOssProperties.bucketName, path, articleCover.getInputStream());

        ossClient.putObject(putObjectRequest);


        return AliOssProperties.aliOssAddress + path;
    }

    @Override
    public void uploadArticleDetails(UploadArticleDetailsVo detailsVo, Long userId) {
        // 查询分区Id是否存在
        if (!zoneApiService.zoneIsExisted(detailsVo.getZoneId())) {
            throw new MyTopException(ExceptionContent.VALIDATION_EXCEPTION.getCode(), "非法分区");
        }

        VideoPo video = new VideoPo();
        BeanUtils.copyProperties(detailsVo, video);
        video.setUserId(userId);
        video.setUploadingDate(new Date());
        video.setStatus(ProductModuleConstant.VideoConstant.WAITING_PASSING);
        videoApiService.save(video);
    }

    @Override
    public void reuploadArticle(Long articleId, Long userId) {
        videoApiService.update(new UpdateWrapper<VideoPo>()
                .set("status", ProductModuleConstant.VideoConstant.WAITING_PASSING)
                .eq("user_id", userId)
                .eq("id", articleId));
    }
}

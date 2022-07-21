package icu.yujing.product.app.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.utils.R;
import icu.yujing.common.utils.YujingUtils;
import icu.yujing.product.app.product.entity.po.VideoPo;
import icu.yujing.product.app.product.entity.vo.AuthorArticlesPageVo;
import icu.yujing.product.app.product.entity.vo.UploadArticleDetailsVo;
import icu.yujing.product.app.product.service.AuthorApiService;
import icu.yujing.product.constant.VideoConstant;
import icu.yujing.product.security.filters.CheckUserLoginStatusByJwtFilter;
import icu.yujing.product.validation.group.VideoDetailsGroup;
import icu.yujing.user.entity.po.UserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/3/27
 * @version: 1.0
 * @description: 用户上传视频等相关功能
 */
@RestController
@RequestMapping("/api/author")
public class AuthorApiController {

    @Autowired
    private AuthorApiService authorApiService;

    /**
     * 用户上传的视频的一部分
     *
     * @param fileSlice   视频分片
     * @param uploadingId 当前视频分片所属的视频上传ID
     * @return
     * @throws IOException
     */
    @PostMapping("/upload/article/slice")
    public R uploadArticleSlice(@RequestParam("file") MultipartFile fileSlice,
                                @RequestParam(value = "uploadingId", required = false) String uploadingId) throws IOException {
        // 校验文件
        String fileUploadingId = authorApiService.uploadArticleSlice(fileSlice, uploadingId);
        return R.ok().putData(fileUploadingId);
    }

    /**
     * 视频分片全部上传完成时调用的接口,用于结束并合并此视频上传ID对应的视频的所有分片
     *
     * @param uploadingId 视频上传ID
     * @return
     */
    @PostMapping("/upload/article/end")
    public R uploadArticleSlice(@RequestParam("uploadingId") String uploadingId) {
        if (StringUtils.isEmpty(uploadingId)) {
            return R.error(ExceptionContent.VALIDATION_EXCEPTION.getCode(), "上传Id不能为空");
        }
        String articleUrl = authorApiService.uploadArticleEnd(uploadingId);
        return R.ok().putData(articleUrl);
    }

    /**
     * 上传视频封面
     *
     * @param articleCover 视频封面文件
     * @return
     * @throws IOException
     */
    @PostMapping("/upload/article/cover")
    public R uploadArticleCover(@RequestParam("cover") MultipartFile articleCover) throws IOException {
        // 校验文件
        YujingUtils.checkFileSizeThrowException(articleCover.getSize(), VideoConstant.allowedVideoCoverFileSize, YujingUtils.SizeUnit.MB);
        YujingUtils.checkFileTypeThrowException(articleCover.getOriginalFilename(), VideoConstant.allowedVideoCoverFileType);

        String articleCoverUrl = authorApiService.uploadArticleCover(articleCover);
        return R.ok().putData(articleCoverUrl);
    }

    /**
     * 上传视频的详细信息(标题、分区等等)
     *
     * @param detailsVo
     * @return
     */
    @PostMapping("/upload/article/details")
    public R uploadArticleDetails(@RequestBody @Validated(VideoDetailsGroup.class) UploadArticleDetailsVo detailsVo) {
        Long userId = ((UserPo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        authorApiService.uploadArticleDetails(detailsVo, userId);
        return R.ok();
    }

    /**
     * 重新将视频状态设置为待审核(用于视频被下架等等因素,用户重新申请将此视频审核)
     *
     * @param articleId
     * @return
     */
    @GetMapping("/reupload/article/{articleId}")
    public R reuploadArticle(@PathVariable("articleId") Long articleId) {
        Long userId = ((UserPo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        authorApiService.reuploadArticle(articleId, userId);
        return R.ok();
    }

    /**
     * 根据检索条件获取当前用户的视频
     *
     * @param authorArticlesPageVo 检索条件(非法判断在service层中)
     * @return
     */
    @GetMapping("/articles")
    public R getArticlesByAuthor(AuthorArticlesPageVo authorArticlesPageVo) {
        Long userId = ((UserPo) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Page<VideoPo> page = authorApiService.getArticlesByAuthor(authorArticlesPageVo, userId);
        return R.ok().putData(page);
    }
}

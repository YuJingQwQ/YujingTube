package icu.yujing.product.app.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.common.constant.ElasticsearchConstant;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.constant.ProductModuleConstant;
import icu.yujing.common.exception.MyTopException;
import icu.yujing.common.utils.Query;
import icu.yujing.common.utils.R;
import icu.yujing.product.app.product.service.AdminApiService;
import icu.yujing.product.app.product.service.VideoApiService;
import icu.yujing.product.constant.VideoConstant;
import icu.yujing.product.app.product.entity.po.VideoPo;
import icu.yujing.product.app.product.entity.to.ElasticSearchVideoTo;
import icu.yujing.product.app.product.entity.vo.AuthorArticlesPageVo;
import icu.yujing.product.feign.UserFeignService;
import icu.yujing.user.entity.po.UserPo;
import io.seata.spring.annotation.GlobalTransactional;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author: Cyqurt
 * @date: 2022/3/27
 * @version: 1.0
 * @description:
 */
@Service
public class AdminApiServiceImpl implements AdminApiService {

    @Autowired
    private RestHighLevelClient elasticClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private VideoApiService videoApiService;

    @Autowired
    private UserFeignService userFeignService;

    @Override
    public Page<VideoPo> getArticlesOfAuthorsByAdmin(AuthorArticlesPageVo params) {
        // 固定 10个每页
        Page<VideoPo> page = new Page<VideoPo>(params.getPage(), 10L);

        QueryWrapper<VideoPo> wrapper = new QueryWrapper<>();

        Query.order(params.getOrderField(), params.getOrderType(), Arrays.asList("uploading_date"), wrapper);

        if (params.getStatus() != null) {
            wrapper.eq("status", params.getStatus());
        }
        if (params.getZoneId() != null) {
            wrapper.eq("zone_id", params.getZoneId());
        }

        Page<VideoPo> pg = videoApiService.page(page, wrapper);
        List<VideoPo> records = pg.getRecords();
        long[] videoIds = new long[records.size()];
        for (int i = 0; i < records.size(); i++) {
            videoIds[i] = records.get(i).getId();
        }

        Map<Long, Long> viewsMap = videoApiService.multiGetViewsOrLikes(videoIds, 0);

        for (VideoPo record : records) {
            record.setViews(viewsMap.get(record.getId()));
        }

        if ("views".equals(params.getOrderField())) {
            if (params.getOrderType() == 0) {
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

    @GlobalTransactional
    @Override
    public void pass(Long articleId) throws IOException {
        // 查询视频内容保存到elasticsearch
        VideoPo video = videoApiService.getOne(new QueryWrapper<VideoPo>().eq("id", articleId));

        R<UserPo> userR = userFeignService.getUserInfo(video.getUserId());
        if (userR.getCode() != 200) {
            throw new MyTopException(ExceptionContent.DIY_EXCEPTION.getCode(), "获取用户信息失败");
        }
        UserPo author = userR.getDataOfJsonObject(new TypeReference<UserPo>() {
        });

        ElasticSearchVideoTo videoTo = new ElasticSearchVideoTo(articleId, video.getCoverUrl(), video.getTitle(), video.getDescription(), video.getReleasingDate(), author.getId(), author.getNickname(), author.getAvatar(), video.getZoneId());
        IndexRequest indexRequest = new IndexRequest(ElasticsearchConstant.VIDEO_DATABASE);
        indexRequest.source(JSON.toJSONString(videoTo), XContentType.JSON);
        IndexResponse response = elasticClient.index(indexRequest, RequestOptions.DEFAULT);
        // 将视频内容保存到Redis中
        Date releasingDate = new Date();
        video.setElasticsearchId(response.getId());
        video.setReleasingDate(releasingDate);

        redisTemplate.opsForValue().set(VideoConstant.VIDEO_DETAILS_REDIS_KEY + video.getId(),
                JSON.toJSONString(video));

        // 修改视频状态
        videoApiService.update(new UpdateWrapper<VideoPo>()
                .set("status", ProductModuleConstant.VideoConstant.RELEASED)
                .set("elasticsearch_id", response.getId())
                .set("releasing_date", releasingDate)
                .eq("id", articleId));
    }

    @GlobalTransactional
    @Override
    public void off(Long articleId) throws IOException {

        // 可能出现空指针异常
        VideoPo video = videoApiService.getOne(new QueryWrapper<VideoPo>().select("elasticsearch_id").eq("id", articleId));
        if (video == null) {
            throw new MyTopException(ExceptionContent.NO_SUCH_VIDEO.getCode(),
                    ExceptionContent.NO_SUCH_VIDEO.getMessage());
        }

        // 删除elasticsearch保存的对应的视频
        DeleteRequest deleteRequest = new DeleteRequest(ElasticsearchConstant.VIDEO_DATABASE, video.getElasticsearchId());
        elasticClient.delete(deleteRequest, RequestOptions.DEFAULT);

        // 删除Redis中的缓存
        redisTemplate.delete(VideoConstant.VIDEO_DETAILS_REDIS_KEY + articleId);

        // 修改视频状态
        videoApiService.update(new UpdateWrapper<VideoPo>()
                .set("status", ProductModuleConstant.VideoConstant.UNPASSING)
                .set("elasticsearch_id", null)
                .eq("id", articleId));
    }

    @Override
    public void initCache() {
        List<VideoPo> videos = videoApiService.list(new QueryWrapper<VideoPo>().select("id", "views", "likes"));
        Map<String, Object> viewsMap = new HashMap<>(videos.size());
        Map<String, Object> likesMap = new HashMap<>(videos.size());
        for (VideoPo video : videos) {
            viewsMap.put(VideoConstant.VIDEO_VIEWS_REDIS_KEY + video.getId(), video.getViews());
            likesMap.put(VideoConstant.VIDEO_LIKES_REDIS_KEY + video.getId(), video.getLikes());
        }

        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();

        opsForValue.multiSet(viewsMap);
        opsForValue.multiSet(likesMap);
    }
}

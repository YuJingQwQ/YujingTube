package icu.yujing.product.app.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.common.constant.ElasticsearchConstant;
import icu.yujing.product.app.product.entity.to.ElasticSearchVideoTo;
import icu.yujing.product.app.product.service.VideoApiService;
import icu.yujing.product.app.product.service.VideoSearchApiService;
import icu.yujing.product.feign.UserFeignService;
import icu.yujing.user.service.UserApiService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
public class VideoSearchApiServiceImpl implements VideoSearchApiService {

    @Autowired
    private RestHighLevelClient elasticClient;

    @Autowired
    private VideoApiService videoApiService;

    @Override
    public Page<ElasticSearchVideoTo> results(String query, Integer page) throws IOException, ExecutionException, InterruptedException {
//        int index = (page - 1) * 10;
        SearchSourceBuilder builder = new SearchSourceBuilder();
        MatchQueryBuilder videoTitle = QueryBuilders.matchQuery("videoTitle", query);
        builder.query(videoTitle);
        // 当数据量多时再做打算
        //TODO 前端传来 index(index = index + size)
//        builder.from(index);
//        builder.size(10);
        SearchRequest searchRequest = new SearchRequest(ElasticsearchConstant.VIDEO_DATABASE);
        searchRequest.source(builder);
        SearchResponse response = elasticClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        Page<ElasticSearchVideoTo> pg;
        if (hits.getHits().length == 0) {
            pg = new Page<>(1, 0, 0);
            pg.setPages(0);
            return pg;
        }

        long[] videoIds = new long[hits.getHits().length];
        SearchHit[] hitsHits = hits.getHits();
        List<ElasticSearchVideoTo> videos = new ArrayList(hitsHits.length);
        for (int i = 0; i < hitsHits.length; i++) {
            ElasticSearchVideoTo elasticSearchVideoTo = JSON.parseObject(hitsHits[i].getSourceAsString(), new TypeReference<ElasticSearchVideoTo>() {
            });
            videoIds[i] = elasticSearchVideoTo.getVideoId();
            videos.add(elasticSearchVideoTo);
        }

        // 查询视频播放量
        Map<Long, Long> videoViewsMap = videoApiService.multiGetViewsOrLikes(videoIds, 0);

        for (ElasticSearchVideoTo video : videos) {
            video.setVideoViews(videoViewsMap.get(video.getVideoId()));
        }

        pg = new Page<>(1, videos.size(), videos.size());
        pg.setPages(1);
        pg.setRecords(videos);
        return pg;
    }
}

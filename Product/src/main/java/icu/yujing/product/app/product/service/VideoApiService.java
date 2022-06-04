package icu.yujing.product.app.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.yujing.product.app.product.entity.po.VideoPo;
import icu.yujing.product.app.product.entity.vo.PageVideoVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
public interface VideoApiService extends IService<VideoPo> {

    List<PageVideoVo> indexVideos() throws IOException, ExecutionException, InterruptedException;

    PageVideoVo getPageVideo(Long videoId) throws ExecutionException, InterruptedException;

    void videoPlay(Long videoId);

    Long getVideoViewsOrLikes(Long videoId, Integer type);

    void videoViewsOrLikesIncrement(Long videoId, Integer type);

    /**
     *
     * @param videoIds
     * @param type type == 0 获取视频播放量, type == 1 获取视频点赞数
     * @return
     */
    Map<Long, Long> multiGetViewsOrLikes(long[] videoIds, Integer type);

    List<PageVideoVo> listRandomlyByZoneIdLimitByCount(Long zoneId, Integer count) throws ExecutionException, InterruptedException;

    List<PageVideoVo> getTheVideosOfTheCurrentUser(Long userId, Long index, Long size, String orderField, Integer orderType);

    long[] listIdByIndexAndSize(long index, long size);
}

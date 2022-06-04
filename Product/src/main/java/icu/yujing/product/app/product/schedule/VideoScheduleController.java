package icu.yujing.product.app.product.schedule;

import icu.yujing.product.app.product.entity.po.VideoPo;
import icu.yujing.product.app.product.service.VideoApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class VideoScheduleController {

    @Lazy
    @Autowired
    private VideoApiService videoApiService;

    @Lazy
    @Autowired
    private StringRedisTemplate redisTemplate;

    private volatile long index = 0L;
    private volatile long size = 100L;

    // 定时任务,将视频播放量和点赞量保存到数据库中
    @Scheduled(cron = "0/59 * * * * *")
    public void updateVideoDetailsInRedisIntoDatasource() {
        // 每次从数据库查询 size 个id
        long[] ids = videoApiService.listIdByIndexAndSize(index, size);
        log.debug("定时任务:-> {}", ids);
        synchronized (VideoScheduleController.class) {
            if (ids == null) {
                index = 0L;
                size = 100L;
                return;
            } else {
                index = size;
                size += 100L;
            }
        }

        // 通过id查询视频的点赞和观看数
        Map<Long, Long> viewsMap = videoApiService.multiGetViewsOrLikes(ids, 0);
        Map<Long, Long> likesMap = videoApiService.multiGetViewsOrLikes(ids, 1);

        // 将点赞数和观看数同步到数据库中
        List<VideoPo> videos = new ArrayList<>();
        for (long id : ids) {
            VideoPo video = new VideoPo();
            video.setId(id);
            video.setViews(viewsMap.get(id));
            video.setLikes(likesMap.get(id));
            videos.add(video);
        }
        videoApiService.updateBatchById(videos);

    }
}

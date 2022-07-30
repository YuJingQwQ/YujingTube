package icu.yujing.product.app.product.schedule;

import icu.yujing.product.app.product.dao.VideoDao;
import icu.yujing.product.constant.VideoConstant;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class VideoScheduleController {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private VideoDao videoDao;

    private volatile long index = 0L;
    private volatile long size = 100L;

    @GlobalTransactional
    // 定时任务,将视频播放量和点赞量保存到数据库中
    @Scheduled(cron = "0/59 * * * * *")
    public void updateVideoDetailsInRedisIntoDatasource() {
        // 每次从数据库查询 size 个id
        List<Long> videoIds = videoDao.listIdByIndexAndSize(index, size);
        log.debug("定时任务:-> {}", videoIds);
        synchronized (VideoScheduleController.class) {
            if (videoIds == null || videoIds.size() == 0) {
                index = 0L;
                size = 100L;
                return;
            } else {
                index = size;
                size += 100L;
            }
        }

        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        List<String> keysForSearchViews = new ArrayList<>(videoIds.size());
        List<String> keysForSearchLikes = new ArrayList<>(videoIds.size());
        for (long id : videoIds) {
            keysForSearchViews.add(VideoConstant.VIDEO_VIEWS_REDIS_KEY + id);
            keysForSearchLikes.add(VideoConstant.VIDEO_LIKES_REDIS_KEY + id);
        }
        List<String> views = opsForValue.multiGet(keysForSearchViews);
        List<String> likes = opsForValue.multiGet(keysForSearchLikes);

        List<synchronizeVideoPO> syns = new ArrayList<>(videoIds.size());
        for (int i = 0; i < videoIds.size(); i++) {
            if (views.get(i) != null || likes.get(i) != null) {
                synchronizeVideoPO syn = new synchronizeVideoPO();
                syn.videoId = videoIds.get(i);
                syn.views = views.get(i);
                syn.likes = likes.get(i);
                syns.add(syn);
            }
        }

        if (syns.size() == 0){
            return;
        }

        videoDao.synchronizeViewsAndLikesOfVideos(syns);
        if (keysForSearchViews.addAll(keysForSearchLikes)) {
            redisTemplate.delete(keysForSearchViews);
        } else {
            redisTemplate.delete(keysForSearchViews);
            redisTemplate.delete(keysForSearchLikes);
        }
    }

    public class synchronizeVideoPO {
        private Object videoId;
        private Object views;
        private Object likes;

        public Object getVideoId() {
            return videoId;
        }

        public void setVideoId(Object videoId) {
            this.videoId = videoId;
        }

        public Object getViews() {
            return views;
        }

        public void setViews(Object views) {
            this.views = views;
        }

        public Object getLikes() {
            return likes;
        }

        public void setLikes(Object likes) {
            this.likes = likes;
        }
    }

    private List<synchronizeVideoPO> multiGetViewsOrLikes(long[] videoIds, Integer type) {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        List<String> keyList = new ArrayList<>(videoIds.length);

        if (type == 0) {
            for (Long videoId : videoIds) {
                keyList.add(VideoConstant.VIDEO_VIEWS_REDIS_KEY + videoId);
            }
        } else {
            for (Long videoId : videoIds) {
                keyList.add(VideoConstant.VIDEO_LIKES_REDIS_KEY + videoId);
            }
        }

        List<String> valueList = opsForValue.multiGet(keyList);
        if (valueList == null || valueList.size() == 0) {
            return null;
        }
        ArrayList<synchronizeVideoPO> synchronizeVideoPOS = new ArrayList<>(valueList.size());
        for (int i = 0; i < videoIds.length; i++) {
            Object result = valueList.get(i);
            if (result != null) {
                synchronizeVideoPO synchronizeVideoPO = new synchronizeVideoPO();
                synchronizeVideoPO.videoId = videoIds[i];
                synchronizeVideoPO.views = valueList.get(i);
                synchronizeVideoPOS.add(synchronizeVideoPO);
            }
        }
        return synchronizeVideoPOS;
    }
}

package icu.yujing.product.app.product.schedule;

import icu.yujing.product.app.product.service.VideoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class VideoScheduleController {

    @Autowired
    private VideoApiService videoApiService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private long index = 0L;
    private long size = 100L;

    //TODO 定时任务,将视频播放量和点赞量保存到数据库中
    public void updateVideoDetailsInRedisIntoDatasource(){
        // 每次从数据库查询 size 个id
        long[] ids = videoApiService.listIdByIndexAndSize(index,size);

        // 通过id查询视频的点赞和观看数

        // 将点赞数和观看数同步到数据库中
    }
}

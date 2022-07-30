package icu.yujing.product.app.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.yujing.product.app.product.entity.po.VideoPo;
import icu.yujing.product.app.product.schedule.VideoScheduleController;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author: Cyqurt
 * @date: 2022/3/19
 * @version: 1.0
 * @description:
 */
@Repository
public interface VideoDao extends BaseMapper<VideoPo> {

    void videoPlayIncrease(@Param("videoId") Long videoId);

    List<VideoPo> listRandomlyByZoneIdLimitByCount(@Param("zoneId") Long zoneId, @Param("count") Integer count);

    List<VideoPo> getTheVideosOfTheCurrentUser(@Param("userId") Long userId, @Param("index") Long index, @Param("size") Long size, @Param("orderField") String orderField, @Param("orderType") Integer orderType);

    List<Long> listIdByIndexAndSize(@Param("index") long index, @Param("size") long size);


    void synchronizeViewsAndLikesOfVideos(@Param("syns") List<VideoScheduleController.synchronizeVideoPO> syns);
}

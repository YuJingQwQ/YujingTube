package icu.yujing.product.app.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.yujing.product.app.product.entity.po.VideoPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/3/19
 * @version: 1.0
 * @description:
 */
public interface VideoDao extends BaseMapper<VideoPo> {

    void videoPlayIncrease(@Param("videoId") Long videoId);

    List<VideoPo> listRandomlyByZoneIdLimitByCount(@Param("zoneId") Long zoneId, @Param("count") Integer count);

    List<VideoPo> getTheVideosOfTheCurrentUser(@Param("userId") Long userId, @Param("index") Long index, @Param("size") Long size, @Param("orderField") String orderField, @Param("orderType") Integer orderType);

    long[] listIdByIndexAndSize(@Param("index") long index, @Param("size") long size);
}

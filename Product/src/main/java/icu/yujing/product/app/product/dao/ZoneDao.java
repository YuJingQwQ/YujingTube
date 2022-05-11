package icu.yujing.product.app.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.yujing.product.app.product.entity.po.ZonePo;

/**
 * @author: Cyqurt
 * @date: 2022/3/19
 * @version: 1.0
 * @description:
 */
public interface ZoneDao extends BaseMapper<ZonePo> {
    Long zoneIsExisted(Long zoneId);
}

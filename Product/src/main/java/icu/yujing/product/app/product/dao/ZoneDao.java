package icu.yujing.product.app.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.yujing.product.app.product.entity.po.ZonePo;
import org.springframework.stereotype.Repository;

/**
 * @author: Cyqurt
 * @date: 2022/3/19
 * @version: 1.0
 * @description:
 */
@Repository
public interface ZoneDao extends BaseMapper<ZonePo> {
    Long zoneIsExisted(Long zoneId);
}

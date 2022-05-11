package icu.yujing.product.app.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.yujing.product.app.product.entity.po.ZonePo;

/**
 * @author: Cyqurt
 * @date: 2022/3/19
 * @version: 1.0
 * @description:
 */
public interface ZoneApiService extends IService<ZonePo> {
    boolean zoneIsExisted(Long zoneId);
}

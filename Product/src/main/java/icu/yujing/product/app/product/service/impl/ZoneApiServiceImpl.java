package icu.yujing.product.app.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yujing.product.app.product.dao.ZoneDao;
import icu.yujing.product.app.product.service.ZoneApiService;
import icu.yujing.product.app.product.entity.po.ZonePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
public class ZoneApiServiceImpl extends ServiceImpl<ZoneDao, ZonePo> implements ZoneApiService {

    @Autowired
    private ZoneDao zoneDao;

    @Override
    public boolean zoneIsExisted(Long zoneId) {
        return zoneDao.zoneIsExisted(zoneId) != null;
    }
}

package icu.yujing.product.app.product.controller;

import icu.yujing.common.utils.R;
import icu.yujing.product.app.product.service.ZoneApiService;
import icu.yujing.product.app.product.entity.po.ZonePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api")
public class ZoneApiController {

    @Autowired
    private ZoneApiService zoneApiService;

    @GetMapping("/zones")
    public R zones() {
        List<ZonePo> zones = zoneApiService.list();
        return R.ok().putData(zones);
    }

}

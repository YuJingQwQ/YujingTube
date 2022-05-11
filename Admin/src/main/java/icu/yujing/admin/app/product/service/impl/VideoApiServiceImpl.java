package icu.yujing.admin.app.product.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yujing.admin.app.product.dao.VideoDao;
import icu.yujing.admin.app.product.entity.po.VideoPo;
import icu.yujing.admin.app.product.service.VideoApiService;
import org.springframework.stereotype.Service;

/**
 * @author: Cyqurt
 * @date: 2022/4/13
 * @version: 1.0
 * @description:
 */
@DS("product")
@Service
public class VideoApiServiceImpl extends ServiceImpl<VideoDao, VideoPo> implements VideoApiService {
}

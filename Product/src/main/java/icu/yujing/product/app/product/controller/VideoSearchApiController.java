package icu.yujing.product.app.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.common.utils.R;
import icu.yujing.product.app.product.entity.to.ElasticSearchVideoTo;
import icu.yujing.product.app.product.service.VideoSearchApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api")
public class VideoSearchApiController {

    @Autowired
    private VideoSearchApiService videoSearchApiService;

    @GetMapping("/results")
    public R results(@RequestParam(value = "query", required = false) String query,
                     @RequestParam(value = "page", defaultValue = "1") Integer page) throws IOException, ExecutionException, InterruptedException {
       Page<ElasticSearchVideoTo> pg = videoSearchApiService.results(query,page);
       return R.ok().putData(pg);
    }

}

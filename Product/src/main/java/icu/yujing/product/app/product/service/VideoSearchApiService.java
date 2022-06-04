package icu.yujing.product.app.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.product.app.product.entity.to.ElasticSearchVideoTo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author: Cyqurt
 * @date: 2022/3/22
 * @version: 1.0
 * @description:
 */
public interface VideoSearchApiService {

    Page<ElasticSearchVideoTo> results(String query, Integer page) throws IOException, ExecutionException, InterruptedException;
}

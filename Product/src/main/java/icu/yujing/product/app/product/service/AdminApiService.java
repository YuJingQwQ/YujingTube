package icu.yujing.product.app.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.product.app.product.entity.po.VideoPo;
import icu.yujing.product.app.product.entity.vo.AuthorArticlesPageVo;

import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/3/27
 * @version: 1.0
 * @description:
 */
public interface AdminApiService {
    Page<VideoPo> getArticlesOfAuthorsByAdmin(AuthorArticlesPageVo params);

    void pass(Long articleId) throws IOException;

    void off(Long articleId) throws IOException;

    void initCache();

}

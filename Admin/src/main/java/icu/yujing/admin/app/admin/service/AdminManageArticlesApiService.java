package icu.yujing.admin.app.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.admin.app.product.entity.po.VideoPo;
import icu.yujing.admin.app.admin.entity.vo.AuthorArticlesPageVo;

import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/4/13
 * @version: 1.0
 * @description:
 */
public interface AdminManageArticlesApiService {
    Page<VideoPo> getArticlesOfAuthorsByAdmin(AuthorArticlesPageVo params);

    void pass(Long articleId) throws IOException;

    void off(Long articleId) throws IOException;

    void initCache();

}

package icu.yujing.admin.app.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.admin.app.product.entity.po.VideoPo;
import icu.yujing.admin.app.admin.entity.vo.AuthorArticlesPageVo;
import icu.yujing.admin.app.admin.service.AdminManageArticlesApiService;
import icu.yujing.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/4/13
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api")
public class AdminManageArticlesApiController {
    @Autowired
    private AdminManageArticlesApiService adminManageArticlesApiService;

    @GetMapping("/articles/of/authors")
    public R getArticlesOfAuthorsByAdmin(AuthorArticlesPageVo params) {
        Page<VideoPo> page = adminManageArticlesApiService.getArticlesOfAuthorsByAdmin(params);
        return R.ok().putData(page);
    }

    @GetMapping("/pass/article/of/authors/{articleId}")
    public R passArticleOfAuthors(@PathVariable("articleId") Long articleId) throws IOException {
        adminManageArticlesApiService.pass(articleId);
        return R.ok();
    }

    @GetMapping("/off/article/of/authors/{articleId}")
    public R offArticleOfAuthors(@PathVariable("articleId") Long articleId) throws IOException {
        adminManageArticlesApiService.off(articleId);
        return R.ok();
    }

    @GetMapping("/initCache")
    public R initCache(){
        adminManageArticlesApiService.initCache();
        return R.ok();
    }

}

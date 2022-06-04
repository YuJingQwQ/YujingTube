package icu.yujing.product.app.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.common.utils.R;
import icu.yujing.product.app.product.service.AdminApiService;
import icu.yujing.product.app.product.entity.po.VideoPo;
import icu.yujing.product.app.product.entity.vo.AuthorArticlesPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description: 功能已搬至Admin模块
 */
@Deprecated
@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    @Autowired
    private AdminApiService adminApiService;


    @GetMapping("/articles/of/authors")
    public R getArticlesOfAuthorsByAdmin(AuthorArticlesPageVo params) {
        Page<VideoPo> page = adminApiService.getArticlesOfAuthorsByAdmin(params);
        return R.ok().putData(page);
    }

    @GetMapping("/pass/article/of/authors/{articleId}")
    public R passArticleOfAuthors(@PathVariable("articleId") Long articleId) throws IOException {
        adminApiService.pass(articleId);
        return R.ok();
    }

    @GetMapping("/off/article/of/authors/{articleId}")
    public R offArticleOfAuthors(@PathVariable("articleId") Long articleId) throws IOException {
        adminApiService.off(articleId);
        return R.ok();
    }

    @GetMapping("/initCache")
    public R initCache(){
        adminApiService.initCache();
        return R.ok();
    }
}

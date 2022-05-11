package icu.yujing.product.app.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.yujing.product.app.product.entity.po.VideoPo;
import icu.yujing.product.app.product.entity.vo.AuthorArticlesPageVo;
import icu.yujing.product.app.product.entity.vo.UploadArticleDetailsVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author: Cyqurt
 * @date: 2022/3/27
 * @version: 1.0
 * @description:
 */
public interface AuthorApiService {
    Page<VideoPo> getArticlesByAuthor(AuthorArticlesPageVo authorArticlesPageVo, Long userId);

    String uploadArticleSlice(MultipartFile fileSlice, String uploadingId) throws IOException;

    String uploadArticleEnd(String uploadingId);

    String uploadArticleCover(MultipartFile articleCover) throws IOException;

    void uploadArticleDetails(UploadArticleDetailsVo videoVo, Long userId);

    void reuploadArticle(Long articleId, Long userId);
}

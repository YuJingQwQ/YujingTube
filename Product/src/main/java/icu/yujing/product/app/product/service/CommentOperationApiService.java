package icu.yujing.product.app.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.yujing.product.app.product.entity.po.CommentOperationPo;

import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/3/24
 * @version: 1.0
 * @description:
 */
public interface CommentOperationApiService extends IService<CommentOperationPo> {
    void operation(Long videoId, Long commentId, Long userId, Integer operation);

    List<CommentOperationPo> getMyOperations(Long videoId, Long userId);

    List<CommentOperationPo> getMyOperations(List<Long> commentIds, Long userId);

    void operationForComment(Integer operation, Long videoId, Long commentId, Long userId);
}

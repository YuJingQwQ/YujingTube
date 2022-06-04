package icu.yujing.product.app.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yujing.product.app.product.service.CommentApiService;
import icu.yujing.product.app.product.service.CommentOperationApiService;
import icu.yujing.product.app.product.dao.CommentOperationDao;
import icu.yujing.product.app.product.entity.po.CommentOperationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
public class CommentOperationApiServiceImpl extends ServiceImpl<CommentOperationDao, CommentOperationPo> implements CommentOperationApiService {

    @Autowired
    private CommentApiService commentApiService;

    @Override
    public void operation(Long videoId, Long commentId, Long userId, Integer operation) {

    }

    @Override
    public List<CommentOperationPo> getMyOperations(Long videoId, Long userId) {
        return this.list(new QueryWrapper<CommentOperationPo>()
                .select("comment_id", "operation")
                .eq("video_id", videoId)
                .eq("user_id", userId)
                .eq("is_deleted", 0));
    }

    @Override
    public List<CommentOperationPo> getMyOperations(List<Long> commentIds, Long userId) {
        return this.list(new QueryWrapper<CommentOperationPo>()
                .select("comment_id", "operation")
                .in("comment_id", commentIds)
                .eq("user_id", userId)
                .eq("is_deleted", 0));
    }

    /**
     * 因为数据不重要,为了性能可以让数据不一致
     *
     * @param operation
     * @param videoId
     * @param commentId
     * @param userId
     */
    @Override
    public void operationForComment(Integer operation, Long videoId, Long commentId, Long userId) {
        // 查询用户对此条评论的操作记录
        CommentOperationPo operationPo = this.getOne(new QueryWrapper<CommentOperationPo>()
                .eq("comment_id", commentId)
                .eq("user_id", userId));

        boolean isUpdate = true;
        Integer ups = 0;
        Integer downs = 0;
        Integer isDeleted = 0;
        if (operationPo == null) {
            // 用户从未对此评论操作过
            operationPo = new CommentOperationPo();
            operationPo.setUserId(userId);
            operationPo.setVideoId(videoId);
            operationPo.setCommentId(commentId);
            operationPo.setIsDeleted(0);
            if (operation == 0) {
                ups++;
            } else {
                downs++;
            }
            isUpdate = false;
        } else {
            // 用户曾经对此条评论操作过

            if (operationPo.getIsDeleted() == 1) {
                // 直接操作
                if (operation == 0) {
                    ups++;
                } else {
                    downs++;
                }
                operationPo.setIsDeleted(0);
            } else {
                // 双向操作
                if (Objects.equals(operationPo.getOperation(), operation)) {
                    // 用户想取消点赞或点踩
                    if (operation == 0) {
                        ups -= 1;
                    } else {
                        downs -= 1;
                    }
                    isDeleted = 1;
                } else {
                    // 用户想从点赞到点踩(或相反)
                    if (operation == 0) {
                        ups += 1;
                        downs -= 1;
                    } else {
                        ups -= 1;
                        downs += 1;
                    }
                }
            }

        }
        operationPo.setOperation(operation);
        if (isUpdate) {
            this.update(new UpdateWrapper<CommentOperationPo>()
                    .set("operation", operation)
                    .set("is_deleted", isDeleted)
                    .eq("comment_id", commentId)
                    .eq("user_id", userId));
        } else {
            this.save(operationPo);
        }
        // 修改用户操作

        // 修改评论点赞或点踩数
        commentApiService.changeLikes(commentId, ups, downs);


    }
}

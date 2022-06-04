package icu.yujing.product.app.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.yujing.common.utils.R;
import icu.yujing.product.app.product.entity.po.CommentPo;
import icu.yujing.product.app.product.entity.vo.CommentVo;
import icu.yujing.product.app.product.entity.vo.UserCommentVo;
import icu.yujing.user.entity.po.UserPo;

import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/3/24
 * @version: 1.0
 * @description:
 */
public interface CommentApiService extends IService<CommentPo> {
    void postComment(CommentVo comment, Long userId);

    R getComments(Integer page, Long videoId, UserPo user);

    List<CommentVo> getComments(Long videoId,Integer page);

    void comment(UserCommentVo commentVo,Long userId);

    void changeLikes(Long commentId, Integer ups, Integer downs);
}

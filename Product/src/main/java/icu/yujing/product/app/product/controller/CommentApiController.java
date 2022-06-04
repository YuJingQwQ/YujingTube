package icu.yujing.product.app.product.controller;

import icu.yujing.common.utils.R;
import icu.yujing.product.app.product.entity.po.CommentOperationPo;
import icu.yujing.product.app.product.entity.vo.UserCommentVo;
import icu.yujing.product.app.product.service.CommentApiService;
import icu.yujing.product.app.product.service.CommentOperationApiService;
import icu.yujing.product.security.filters.CheckUserLoginStatusByJwtFilter;
import icu.yujing.user.entity.po.UserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api")
public class CommentApiController {

    @Autowired
    private CommentApiService commentApiService;

    @Autowired
    private CommentOperationApiService commentOperationApiService;

    /**
     * 获取当前视频的评论信息(固定一页为10条)
     * @param page 评论数据页码
     * @param videoId 当前视频ID
     * @return
     */
    @GetMapping("/comments")
    public R getVideoComments(@RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam("videoId") Long videoId) {
        UserPo user = CheckUserLoginStatusByJwtFilter.currentThreadUser.get().getUser();
        return commentApiService.getComments(page, videoId, user);
    }

    /**
     * 已经在/api/comments请求内获取了用户对评论的操作,不需要单独请求
     * @param videoId
     * @return
     */
    @Deprecated
    @GetMapping("/comments/my/operations/{videoId}")
    public R getMyOperations(@PathVariable("videoId") Long videoId) {
        UserPo user = CheckUserLoginStatusByJwtFilter.currentThreadUser.get().getUser();
//        if (user == null) {
//            return R.ok();
//        }
        List<CommentOperationPo> userOperations = commentOperationApiService.getMyOperations(videoId, user.getId());
        return R.ok().putData(userOperations);
    }

    /**
     * 用户对视频进行评论
     *
     * @param commentVo
     * @return
     */
    @PostMapping("/comment")
    public R comment(@RequestBody UserCommentVo commentVo) {
        UserPo user = CheckUserLoginStatusByJwtFilter.currentThreadUser.get().getUser();
        commentApiService.comment(commentVo, user.getId());
        return R.ok();
    }

    /**
     * 用户对评论进行操作(点赞或点踩)
     * @param operation 0:点赞 1:点踩
     * @param videoId 视频id
     * @param commentId 评论id
     * @return
     */
    @GetMapping("/comment/operation/{operation}/{videoId}/{commentId}")
    public R operationForComment(@PathVariable("operation") Integer operation,
                                 @PathVariable("videoId") Long videoId,
                                 @PathVariable("commentId") Long commentId) {
        UserPo user = CheckUserLoginStatusByJwtFilter.currentThreadUser.get().getUser();
        commentOperationApiService.operationForComment(operation, videoId, commentId, user.getId());
        return R.ok();
    }

}

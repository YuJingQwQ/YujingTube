package icu.yujing.product.app.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yujing.common.exception.MyTopException;
import icu.yujing.common.utils.R;
import icu.yujing.product.app.product.dao.CommentDao;
import icu.yujing.product.app.product.service.CommentApiService;
import icu.yujing.product.app.product.service.CommentOperationApiService;
import icu.yujing.product.app.product.entity.po.CommentOperationPo;
import icu.yujing.product.app.product.entity.po.CommentPo;
import icu.yujing.product.app.product.entity.vo.CommentVo;
import icu.yujing.product.app.product.entity.vo.UserCommentVo;
import icu.yujing.product.feign.UserFeignService;
import icu.yujing.user.entity.po.UserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
public class CommentApiServiceImpl extends ServiceImpl<CommentDao, CommentPo> implements CommentApiService {

    @Autowired
    private CommentDao commentDao;

    @Lazy
    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private CommentOperationApiService commentOperationApiService;

    @Override
    public void postComment(CommentVo comment, Long userId) {

    }

    @Override
    public R getComments(Integer page, Long videoId, UserPo user) {
        //TODO 可以使用 union做到查询评论内容和用户对评论内容的操作
        List<CommentVo> comments = this.getComments(videoId, page);
        long commentsCount = this.count(new QueryWrapper<CommentPo>().eq("video_id", videoId));
        Page<CommentVo> commentsPage = new Page<>(page, 10L);
        commentsPage.setRecords(comments);
        commentsPage.setTotal(commentsCount);
        commentsPage.setPages(commentsCount % 10L == 0 ? commentsCount / 10L : commentsCount / 10L + 1L);
        if (comments.size() == 0) {
            return R.ok().putData(commentsPage);
        }

        // 获取评论用户的头像和名字
        long[] theUserIdsOfTheComments = new long[comments.size()];
        Long[] commentIds = new Long[comments.size()];
        int index = 0;

        for (CommentVo comment : comments) {
            theUserIdsOfTheComments[index] = comment.getUserId();
            commentIds[index] = comment.getCommentId();
            index++;
        }
        R<List<UserPo>> usersR = userFeignService.getAvatarsAndNicknamesOfUsers(theUserIdsOfTheComments);
        if (usersR.getCode() != 200) {
            // 待改进, 当获取不到用户头像时,前端使用空白头像等用来代替显示,而不是抛出异常
            throw new MyTopException(88888, "查询数据失败");
        }

        // 获取评论区的用户的信息
        Map<Long, UserPo> usersMap = usersR.getDataOfJsonObject(new TypeReference<List<UserPo>>() {
        }).stream().collect(Collectors.toMap(UserPo::getId, userPo -> userPo));

        if (user != null) {
            // 查询用户对评论的操作
            List<CommentOperationPo> userOperations = commentOperationApiService.getMyOperations(Arrays.asList(commentIds), user.getId());

            if (userOperations == null || userOperations.size() == 0) {

            } else {
                // 用户对评论有操作
                Map<Long, Integer> operationsMap = userOperations.stream()
                        .collect(Collectors.toMap(CommentOperationPo::getCommentId,
                                CommentOperationPo::getOperation));
                comments.stream().forEach(commentVo -> {
                    UserPo commentUser = usersMap.get(commentVo.getUserId());
                    commentVo.setUsername(commentUser.getNickname());
                    commentVo.setUserAvatar(commentUser.getAvatar());
                    commentVo.setMyOperation(operationsMap.get(commentVo.getCommentId()));
                });
                return R.ok().putData(commentsPage);
            }
        }
        // 用户未登录或用户对评论无操作会走到这里
        comments.stream().forEach(commentVo -> {
            UserPo commentUser = usersMap.get(commentVo.getUserId());
            commentVo.setUsername(commentUser.getNickname());
            commentVo.setUserAvatar(commentUser.getAvatar());
        });
        return R.ok().putData(commentsPage);
    }

    @Override
    public List<CommentVo> getComments(Long videoId, Integer page) {
        // 计算起始值  固定size == 10
        Long index = (page - 1) * 10L;
        return commentDao.getComments(videoId, index, 10);
    }

    @Override
    public void comment(UserCommentVo commentVo, Long userId) {
        CommentPo commentPo = new CommentPo();
        commentPo.setComment(commentVo.getComment());
        commentPo.setVideoId(commentVo.getVideoId());
        commentPo.setUserId(userId);
        commentPo.setDate(new Date());
        this.save(commentPo);
    }

    @Override
    public void changeLikes(Long commentId, Integer ups, Integer downs) {
        commentDao.changeLikes(commentId,ups,downs);
    }


}

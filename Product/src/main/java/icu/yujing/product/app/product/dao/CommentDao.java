package icu.yujing.product.app.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.yujing.product.app.product.entity.po.CommentPo;
import icu.yujing.product.app.product.entity.vo.CommentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
public interface CommentDao extends BaseMapper<CommentPo> {
    List<CommentVo> getComments(@Param("videoId") Long videoId, @Param("index") Long index, @Param("size") Integer size);

    void changeLikes(@Param("commentId") Long commentId, @Param("ups") Integer ups, @Param("downs") Integer downs);
}

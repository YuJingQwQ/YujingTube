package icu.yujing.product.app.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.yujing.product.app.product.entity.po.CommentOperationPo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author: Cyqurt
 * @date: 2022/3/24
 * @version: 1.0
 * @description:
 */
@Repository
public interface CommentOperationDao extends BaseMapper<CommentOperationPo> {
    CommentOperationPo selectOne(@Param("videoId") Long videoId, @Param("commentId") Long commentId, @Param("userId") Long userId);

}

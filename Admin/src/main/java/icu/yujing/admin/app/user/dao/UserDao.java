package icu.yujing.admin.app.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.yujing.admin.app.user.entity.po.UserPo;
import org.apache.ibatis.annotations.Param;

/**
 * @author: Cyqurt
 * @date: 2022/3/17
 * @version: 1.0
 * @description:
 */
public interface UserDao extends BaseMapper<UserPo> {
    void increaseVideoCount(@Param("userId") Long userId);

    void subtractVideoCount(@Param("userId") Long userId);
}

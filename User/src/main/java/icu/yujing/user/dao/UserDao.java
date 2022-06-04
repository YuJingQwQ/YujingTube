package icu.yujing.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.yujing.user.entity.po.UserPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: Cyqurt
 * @date: 2022/3/17
 * @version: 1.0
 * @description:
 */
@Mapper
public interface UserDao extends BaseMapper<UserPo> {
}

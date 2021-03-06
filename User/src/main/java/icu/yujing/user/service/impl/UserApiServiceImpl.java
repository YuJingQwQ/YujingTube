package icu.yujing.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yujing.user.dao.UserDao;
import icu.yujing.user.entity.po.UserPo;
import icu.yujing.user.service.UserApiService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
@DubboService(interfaceClass = UserApiService.class)
public class UserApiServiceImpl extends ServiceImpl<UserDao, UserPo> implements UserApiService {

    @Override
    public UserPo getUserFromJwt() {
        return (UserPo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public List<UserPo> getAvatarsAndNicknamesOfUsers(long[] userIds) {
        List<Long> ids = new ArrayList<>(userIds.length);
        for (long userId : userIds) {
            ids.add(userId);
        }
        return list(new QueryWrapper<UserPo>()
                .select("id", "nickname", "avatar")
                .in("id", ids));
    }

    @Override
    public UserPo getUserFromDatabase(Long userId) {
        UserPo user = getById(userId);
        user.setPassword(null);
        return user;
    }
}

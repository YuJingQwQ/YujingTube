package icu.yujing.admin.app.user.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yujing.admin.app.user.dao.UserDao;
import icu.yujing.admin.app.user.entity.po.UserPo;
import icu.yujing.admin.app.user.service.UserApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: Cyqurt
 * @date: 2022/4/13
 * @version: 1.0
 * @description:
 */
@DS("user")
@Service
public class UserApiServiceImpl extends ServiceImpl<UserDao, UserPo> implements UserApiService {
    @Autowired
    private UserDao userDao;
    @Override
    public void increaseVideoCount(Long userId) {
        userDao.increaseVideoCount(userId);
    }

    @Override
    public void subtractVideoCount(Long userId) {
        userDao.subtractVideoCount(userId);
    }
}

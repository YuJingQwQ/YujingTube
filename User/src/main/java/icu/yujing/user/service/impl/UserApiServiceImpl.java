package icu.yujing.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yujing.user.dao.UserDao;
import icu.yujing.user.entity.po.UserPo;
import icu.yujing.user.service.UserApiService;
import org.springframework.stereotype.Service;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
public class UserApiServiceImpl extends ServiceImpl<UserDao, UserPo> implements UserApiService {
}

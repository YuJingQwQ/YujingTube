package icu.yujing.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.yujing.common.security.entity.UserDetailsEntity;
import icu.yujing.user.entity.po.UserPo;

import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
public interface UserApiService extends IService<UserPo> {
    UserPo getUserFromJwt();

    List<UserPo> getAvatarsAndNicknamesOfUsers(long[] userIds);

    UserPo getUserFromDatabase(Long userId);
}

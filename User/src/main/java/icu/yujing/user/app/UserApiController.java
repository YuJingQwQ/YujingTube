package icu.yujing.user.app;

import icu.yujing.common.utils.R;
import icu.yujing.user.entity.po.UserPo;
import icu.yujing.user.service.UserApiService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserApiController {

    @Autowired
    private UserApiService userApiService;

    @GetMapping("/user/from/session")
    public R getUserFromSession() {
        UserPo user = userApiService.getUserFromJwt();
        return user == null ? R.ok().putData(null) : R.ok().putData(user);
    }

    /**
     * 获取用户的昵称和头像地址
     *
     * @param userIds
     * @return
     */
    @GetMapping("/users/avatar/nickname")
    public R getAvatarsAndNicknamesOfUsers(@RequestParam("userIds") long[] userIds) {
        List<UserPo> users = userApiService.getAvatarsAndNicknamesOfUsers(userIds);

        return R.ok().putData(users);
    }

    /**
     * 获取单个用户的信息(不包括敏感信息)
     *
     * @param userId
     * @return
     */
    @GetMapping("/user/{userId}")
    public R getUserInfo(@PathVariable("userId") Long userId) {
        UserPo user = userApiService.getUserFromDatabase(userId);
        return R.ok().putData(user);
    }
}

package icu.yujing.user.app;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import icu.yujing.common.security.entity.UserDetailsEntity;
import icu.yujing.common.utils.R;
import icu.yujing.user.entity.po.UserPo;
import icu.yujing.user.security.filters.CheckUserLoginStatusByJwtFilter;
import icu.yujing.user.service.UserApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/test/authority")
    @PreAuthorize("hasAuthority('test')")
    public R test(){
        return R.ok();
    }


    @GetMapping("/user/from/session")
    public R getUserFromSession() {
        UserDetailsEntity userDetailsEntity = CheckUserLoginStatusByJwtFilter.currentThreadUser.get();
        if (userDetailsEntity == null){
            return R.ok().putData(null);
        }else{
            return R.ok().putData(userDetailsEntity.getUser());
        }
//        SecurityContext context = SecurityContextHolder.getContext();
//        String jwt = request.getHeader(UserModuleConstant.JWT_TOKEN_HTTP_HEADER_KEY);
//        Claims claims = JwtUtils.parseJwt(UserModuleConstant.JWT_USER_SIGNATURE, jwt);
//        String userId = claims.get(UserModuleConstant.JWT_USER_KEY).toString();
//        String userDetailsJson = redisTemplate.opsForValue().get(UserModuleConstant.USER_KEY_PREFIX_IN_REDIS + userId);
//        if (StringUtils.isEmpty(userDetailsJson)) {
//            return R.ok().putData(null);
//        } else {
//            UserDetailsEntity userDetailsEntity = JSON.parseObject(userDetailsJson, new TypeReference<UserDetailsEntity>() {
//            });
//            return R.ok().putData(userDetailsEntity.getUser());
//        }
    }

    /**
     * 获取用户的昵称和头像地址
     *
     * @param userIds
     * @return
     */
    @GetMapping("/users/avatar/nickname")
    public R getAvatarsAndNicknamesOfUsers(@RequestParam("userIds") long[] userIds) {
        List<Long> ids = new ArrayList<>(userIds.length);
        for (long userId : userIds) {
            ids.add(userId);
        }
        List<UserPo> users = userApiService.list(new QueryWrapper<UserPo>()
                .select("id", "nickname", "avatar")
                .in("id", ids));
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
        UserPo user = userApiService.getById(userId);
        user.setPassword(null);
        user.setPhone(null);
        return R.ok().putData(user);
    }
}

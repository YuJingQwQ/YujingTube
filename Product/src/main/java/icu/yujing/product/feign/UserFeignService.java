package icu.yujing.product.feign;

import icu.yujing.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
@FeignClient("yujingtube-user")
@RequestMapping("/api")
public interface UserFeignService {
    @GetMapping("/user/from/session")
    R getUserFromSession();

    @GetMapping("/users/avatar/nickname")
    R getAvatarsAndNicknamesOfUsers(@RequestParam("userIds") long[] userIds);

    @GetMapping("/user/{userId}")
    R getUserInfo(@PathVariable("userId") Long userId);
}

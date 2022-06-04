package icu.yujing.admin.feign;

import icu.yujing.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: Cyqurt
 * @date: 2022/4/13
 * @version: 1.0
 * @description:
 */
@Service
@FeignClient("yujingtube-product")
@RequestMapping("/api")
public interface VideoFeignService {
    @GetMapping("/video/multi_get_views_or_likes")
    R multiGetViewsOrLikes(@RequestParam("videoIds") long[] videoIds,@RequestParam("type") Integer type);
}

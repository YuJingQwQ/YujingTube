package icu.yujing.product.app.product.controller;

import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.exception.MyTopException;
import icu.yujing.common.utils.Query;
import icu.yujing.common.utils.R;
import icu.yujing.product.app.product.entity.vo.PageVideoVo;
import icu.yujing.product.app.product.service.VideoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;


/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/api")
public class VideoApiController {

    @Autowired
    private VideoApiService videoApiService;

    @GetMapping("/index/videos")
    public R mainPageVideos() throws IOException, ExecutionException, InterruptedException {
        List<PageVideoVo> videos = videoApiService.indexVideos();
        Collections.shuffle(videos);
        return R.ok().putData(videos);
    }

    @GetMapping("/videos")
    public R getVideos(@RequestParam(required = false) Long zoneId, Integer count) throws ExecutionException, InterruptedException {
        List<PageVideoVo> videos = videoApiService.listRandomlyByZoneIdLimitByCount(zoneId, count);
        return R.ok().putData(videos);
    }

    @GetMapping("/video/{videoId}")
    public R getVideo(@PathVariable("videoId") Long videoId) throws ExecutionException, InterruptedException {
        PageVideoVo video = videoApiService.getPageVideo(videoId);
        return R.ok().putData(video);
    }

    /**
     * 当视频点击播放时调用的接口
     * 作用: 给当前视频增加播放量
     * 待改进: 如果用户一直调用此接口将无限量增加播放量, 应该给予限制条件(如 每个用户只能给某个视频增加N个播放量等)
     *
     * @param videoId
     * @return
     */
    @GetMapping("/video/play/{videoId}")
    public R playVideo(@PathVariable("videoId") Long videoId) {
        videoApiService.videoPlay(videoId);
        return R.ok();
    }

    static final List<String> ALLOWED_ORDER_FIELDS = Arrays.asList("views", "releasing_date");
    static final Map<String, List<Integer>> ALLOWED_ORDER_TYPES;

    static {
        ALLOWED_ORDER_TYPES = new HashMap<>(ALLOWED_ORDER_FIELDS.size());
        ALLOWED_ORDER_TYPES.put("views", Arrays.asList(0));
        ALLOWED_ORDER_TYPES.put("releasing_date", Arrays.asList(0, 1));
    }

    @GetMapping("/video/get_videos_of_by_user_id")
    public R getTheVideosOfTheCurrentUser(@RequestParam(value = "index", required = false, defaultValue = "0") Long index,
                                          @RequestParam(value = "size", required = false, defaultValue = "10") Long size,
                                          @RequestParam(value = "orderField", required = false, defaultValue = "releasing_date") String orderField,
                                          @RequestParam(value = "orderType", required = false, defaultValue = "0") Integer orderType,
                                          @RequestParam Long userId) {
        if (index < 0) {
            throw new MyTopException(ExceptionContent.DIY_EXCEPTION.getCode(), "非法索引值");
        }
        if (size > 100) {
            throw new MyTopException(ExceptionContent.DIY_EXCEPTION.getCode(), "非法查询个数");
        }

        boolean orderIsValid = Query.orderIsValid(orderField, orderType, ALLOWED_ORDER_FIELDS, ALLOWED_ORDER_TYPES);
        if (!orderIsValid) {
            throw new MyTopException(ExceptionContent.DIY_EXCEPTION.getCode(), "非法排序");
        }

        List<PageVideoVo> videos = videoApiService.getTheVideosOfTheCurrentUser(userId, index, size, orderField, orderType);

        return R.ok().putData(videos);
    }
}

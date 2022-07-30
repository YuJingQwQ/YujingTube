package icu.yujing.product.app.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.constant.ProductModuleConstant;
import icu.yujing.common.exception.MyTopException;
import icu.yujing.product.app.product.dao.VideoDao;
import icu.yujing.product.app.product.entity.po.VideoPo;
import icu.yujing.product.app.product.entity.vo.PageVideoVo;
import icu.yujing.product.app.product.service.VideoApiService;
import icu.yujing.product.constant.VideoConstant;
import icu.yujing.user.entity.po.UserPo;
import icu.yujing.user.service.UserApiService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.client.RestHighLevelClient;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Service
public class VideoApiServiceImpl extends ServiceImpl<VideoDao, VideoPo> implements VideoApiService {

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @DubboReference
    private UserApiService userApiService;

    @Override
    public List<PageVideoVo> indexVideos() {
        List<VideoPo> videos = this.list(new QueryWrapper<VideoPo>()
                .eq("status", ProductModuleConstant.VideoConstant.RELEASED));

        long[] videoIds = new long[videos.size()];
        Set<Long> authorIdSet = new HashSet<>(videos.size());
        for (int i = 0; i < videos.size(); i++) {
            authorIdSet.add(videos.get(i).getUserId());
            videoIds[i] = videos.get(i).getId();
        }
        long[] authorIds = new long[authorIdSet.size()];
        int i = 0;
        for (Long authorId : authorIdSet) {
            authorIds[i++] = authorId;
        }

        // 查询作者信息
        List<UserPo> authors = userApiService.getAvatarsAndNicknamesOfUsers(authorIds);
        final Map<Long, UserPo> authorsMap = authors.stream().collect(Collectors.toMap(UserPo::getId, userPo -> userPo));
        return videos.stream().map(videoPo -> {
            UserPo author = authorsMap.get(videoPo.getUserId());
            String authorNickname;
            String authorAvatar;
            if (author == null) {
                authorNickname = "-";
                authorAvatar = "https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/avatar/avatar.png";
            } else {
                authorNickname = author.getNickname();
                authorAvatar = author.getAvatar();
            }

            return new PageVideoVo(videoPo.getId(),
                    videoPo.getCoverUrl()
                    , videoPo.getVideoUrl(),
                    videoPo.getTitle(),
                    videoPo.getType(),
                    videoPo.getViews(),
                    videoPo.getDescription(),
                    videoPo.getReleasingDate(),
                    videoPo.getZoneId(),
                    videoPo.getUserId(),
                    authorNickname,
                    authorAvatar
            );
        }).collect(Collectors.toList());
    }

    @Override
    public PageVideoVo getPageVideo(Long videoId) {

        Object videoObj = redisTemplate.opsForValue().get(VideoConstant.VIDEO_DETAILS_REDIS_KEY + videoId);
        VideoPo videoPo;
        if (StringUtils.isEmpty(videoObj)) {
            // 分布式重量级锁 RedissonClient
            RLock lock = redissonClient.getLock(VideoConstant.VIDEO_DETAILS_LOCK);
            lock.lock();
            videoObj = redisTemplate.opsForValue().get(VideoConstant.VIDEO_DETAILS_REDIS_KEY + videoId);
            if (StringUtils.isEmpty(videoObj)) {
                // 查询此视频信息
                videoPo = getById(videoId);
                if (videoPo == null) {
                    throw new MyTopException(ExceptionContent.NO_SUCH_VIDEO.getCode(), ExceptionContent.NO_SUCH_VIDEO.getMessage());
                }
                redisTemplate.opsForValue().set(ProductModuleConstant.VideoConstant.VIDEO_DETAILS_REDIS_KEY + videoId,
                        JSON.toJSONString(videoPo));
            } else {
                videoPo = JSON.parseObject(videoObj.toString(), new TypeReference<VideoPo>() {
                });
            }
            lock.unlock();
        } else {
            videoPo = JSON.parseObject(videoObj.toString(), new TypeReference<VideoPo>() {
            });
        }

        UserPo author;
        try {
            author = userApiService.getUserFromDatabase(videoPo.getUserId());
        } catch (Exception e) {
            author = new UserPo();
            author.setNickname("查询失败");
            author.setAvatar("https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/avatar/avatar.png");
            author.setFansCount(0);
        }

        PageVideoVo videoVo = new PageVideoVo();
        videoVo.setVideoId(videoPo.getId());
        videoVo.setVideoCoverUrl(videoPo.getCoverUrl());
        videoVo.setVideoUrl(videoPo.getVideoUrl());
        videoVo.setVideoTitle(videoPo.getTitle());
        videoVo.setVideoType(videoPo.getType());
        videoVo.setVideoDescription(videoPo.getDescription());
        videoVo.setVideoReleasingDate(videoPo.getReleasingDate());
        videoVo.setZoneId(videoPo.getZoneId());
        videoVo.setAuthorId(videoPo.getUserId());
        videoVo.setVideoViews(videoPo.getViews());
        videoVo.setAuthorName(author.getNickname());
        videoVo.setAuthorAvatar(author.getAvatar());
        videoVo.setAuthorFansCount(author.getFansCount());
        return videoVo;
    }

    @Override
    public void videoPlay(Long videoId) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        opsForValue.increment(VideoConstant.VIDEO_VIEWS_REDIS_KEY + videoId, 1L);
    }

    @Override
    public Map<Long, Long> multiGetViewsOrLikes(long[] videoIds, Integer type) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        List<String> keyList = new ArrayList<>(videoIds.length);

        if (type == 0) {
            for (Long videoId : videoIds) {
                keyList.add(VideoConstant.VIDEO_VIEWS_REDIS_KEY + videoId);
            }
        } else {
            for (Long videoId : videoIds) {
                keyList.add(VideoConstant.VIDEO_LIKES_REDIS_KEY + videoId);
            }
        }

        List<Object> viewsOrLikesList = opsForValue.multiGet(keyList);
        Map<Long, Long> map = new HashMap<>();
        List<Long> noResultVideoIds = new ArrayList<>();
        for (int i = 0; i < videoIds.length; i++) {
            Object result = viewsOrLikesList.get(i);
            if (result == null) {
                noResultVideoIds.add(videoIds[i]);
            } else {
                map.put(videoIds[i], Long.parseLong(String.valueOf(viewsOrLikesList.get(i))));
            }
        }
        if (noResultVideoIds.size() > 0) {
            // 查询没有数据的videoIds
            List<VideoPo> videos;
            if (type == 0) {
                videos = this.list(new QueryWrapper<VideoPo>()
                        .select("id", "views")
                        .in("id", noResultVideoIds));
            } else {
                videos = this.list(new QueryWrapper<VideoPo>()
                        .select("id", "likes")
                        .in("id", noResultVideoIds));
            }
            //TODO 不保存非法数据,会一直缓存穿透
//            Map<Long, Long> saveInCacheMap = null;
//            if (videos != null && videos.size() > 0) {
//                if (type == 0) {
//                    saveInCacheMap = videos.stream().collect(Collectors.toMap(VideoPo::getId, VideoPo::getViews));
//                } else {
//                    saveInCacheMap = videos.stream().collect(Collectors.toMap(VideoPo::getId, videoPo -> videoPo.getLikes().longValue()));
//                }
//
//
//            }
            if (videos != null && videos.size() > 0) {
                Map<String, Long> saveInCacheMap = new HashMap<>(videos.size());
                if (type == 0) {
                    for (VideoPo video : videos) {
                        saveInCacheMap.put(VideoConstant.VIDEO_VIEWS_REDIS_KEY + video.getId(), video.getViews());
                        map.put(video.getId(), video.getViews());
                    }
                } else {
                    for (VideoPo video : videos) {
                        saveInCacheMap.put(VideoConstant.VIDEO_LIKES_REDIS_KEY + video.getId(), video.getViews());
                        map.put(video.getId(), video.getLikes());
                    }
                }
                opsForValue.multiSet(saveInCacheMap);
            }
        }
        return map;
    }

    @Override
    public List<PageVideoVo> listRandomlyByZoneIdLimitByCount(Long zoneId, Integer count) throws ExecutionException, InterruptedException {
        // 从数据库查询count个并且对应分区的视频数据
        List<VideoPo> videos = videoDao.listRandomlyByZoneIdLimitByCount(zoneId, count);
        long[] videoIds = new long[videos.size()];
        // 排除相同作者
        Set<Long> authorIdSet = new HashSet<>(videos.size());
        for (int i = 0; i < videos.size(); i++) {
            authorIdSet.add(videos.get(i).getUserId());
            videoIds[i] = videos.get(i).getId();
        }
        long[] authorIds = new long[authorIdSet.size()];
        int i = 0;
        for (Long authorId : authorIdSet) {
            authorIds[i++] = authorId;
        }
        // 查询作者信息
        List<UserPo> authors = userApiService.getAvatarsAndNicknamesOfUsers(authorIds);
        final Map<Long, UserPo> authorsMap = authors.stream().collect(Collectors.toMap(UserPo::getId, userPo -> userPo));
        return videos.stream().map(videoPo -> {
            UserPo author = authorsMap.get(videoPo.getUserId());
            String authorNickname = null;
            String authorAvatar = null;
            if (author == null) {
                // !魔法值!
                authorNickname = "用户不存在";
                authorAvatar = "https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/avatar/avatar.png";
            } else {
                authorNickname = author.getNickname();
                authorAvatar = author.getAvatar();
            }

            return new PageVideoVo(videoPo.getId(),
                    videoPo.getCoverUrl(),
                    videoPo.getVideoUrl(),
                    videoPo.getTitle(),
                    videoPo.getType(),
                    videoPo.getViews(),
                    videoPo.getDescription(),
                    videoPo.getReleasingDate(),
                    videoPo.getZoneId(),
                    videoPo.getUserId(),
                    authorNickname,
                    authorAvatar
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<PageVideoVo> getTheVideosOfTheCurrentUser(Long userId, Long index, Long size, String orderField, Integer orderType) {
        List<VideoPo> videos = videoDao.getTheVideosOfTheCurrentUser(userId, index, size, orderField, orderType);
        if (videos == null || videos.size() == 0) {
            return new ArrayList<>(0);
        }
        List<PageVideoVo> pageVideos = new ArrayList<>(videos.size());
        for (VideoPo video : videos) {
            PageVideoVo pageVideoVo = new PageVideoVo(video.getId(),
                    video.getCoverUrl(),
                    video.getVideoUrl(),
                    video.getTitle(),
                    video.getType(),
                    video.getViews(),
                    video.getDescription(),
                    video.getReleasingDate(),
                    video.getZoneId(),
                    userId,
                    null,
                    null);
            pageVideos.add(pageVideoVo);
        }
        return pageVideos;
    }

}

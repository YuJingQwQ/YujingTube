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
    private ThreadPoolExecutor executor;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @DubboReference()
    private UserApiService userApiService;

//    @Autowired
//    private UserFeignService userFeignService;

    @Override
    public List<PageVideoVo> indexVideos() throws ExecutionException, InterruptedException {
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
        // 查询视频播放量
        CompletableFuture<Map<Long, Long>> videoViewsMapFuture = CompletableFuture.supplyAsync(() -> this.multiGetViewsOrLikes(videoIds, 0), executor);


//        R<List<UserPo>> authorsR = userFeignService.getAvatarsAndNicknamesOfUsers(authorIds);

//        Map<Long, UserPo> authorsMap = null;
//        if (authorsR.getCode() == 200) {
//            List<UserPo> authors = authorsR.getDataOfJsonObject(new TypeReference<List<UserPo>>() {
//            });
//            authorsMap = authors.stream().collect(Collectors.toMap(UserPo::getId, userPo -> userPo));
//        } else {
//            authorsMap = new HashMap<>();
//        }

        // 查询作者信息
        List<UserPo> authors = userApiService.getAvatarsAndNicknamesOfUsers(authorIds);
        final Map<Long, UserPo> authorsMap = authors.stream().collect(Collectors.toMap(UserPo::getId, userPo -> userPo));
        final Map<Long, Long> videoViewsMap = videoViewsMapFuture.get();
        return videos.stream().map(videoPo -> {
            UserPo author = authorsMap.get(videoPo.getUserId());
            String authorNickname = null;
            String authorAvatar = null;
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
                    videoViewsMap.get(videoPo.getId()),
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
    public PageVideoVo getPageVideo(Long videoId) throws ExecutionException, InterruptedException {

        Object videoObj = redisTemplate.opsForValue().get(VideoConstant.VIDEO_DETAILS_REDIS_KEY + videoId);
        VideoPo videoPo = null;
        if (StringUtils.isEmpty(videoObj)) {
            // 查询此视频信息
            //TODO 分布式锁 RedissonClient
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

        CompletableFuture<Long> VideoViewsFuture = CompletableFuture.supplyAsync(() ->
                this.getVideoViewsOrLikes(videoId, 0), executor);

        UserPo author = null;
        try {
            author = userApiService.getUserFromDatabase(videoPo.getUserId());
        } catch (Exception e) {
            author = new UserPo();
            author.setNickname("查询失败");
            author.setAvatar("https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/avatar/avatar.png");
            author.setFansCount(0);
        }

        // 获取作者信息
//        R<UserPo> userR = userFeignService.getUserInfo(videoPo.getUserId());
//        UserPo author = null;
//        if (userR.getCode() == 200) {
//            author = userR.getDataOfJsonObject(new TypeReference<UserPo>() {
//            });
//            if (author == null) {
//                author = new UserPo();
//                author.setNickname("用户已注销");
//                author.setAvatar("https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/avatar/avatar.png");
//                author.setFansCount(0);
//            }
//
//        } else {
//            // 查询作者失败时,返回一个默认对象
//            author = new UserPo();
//            author.setNickname("查询失败");
//            author.setAvatar("https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/avatar/avatar.png");
//            author.setFansCount(0);
//        }

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
        videoVo.setAuthorName(author.getNickname());
        videoVo.setAuthorAvatar(author.getAvatar());
        videoVo.setAuthorFansCount(author.getFansCount());

        Long views = VideoViewsFuture.get();
        videoVo.setVideoViews(views);
        return videoVo;
    }

    @Override
    public void videoPlay(Long videoId) {
        this.videoViewsOrLikesIncrement(videoId, 0);
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
            List<VideoPo> videos = null;
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
        List<VideoPo> videos = videoDao.listRandomlyByZoneIdLimitByCount(zoneId, count);
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
        // 查询视频播放量
        CompletableFuture<Map<Long, Long>> videoViewsMapFuture = CompletableFuture.supplyAsync(() -> this.multiGetViewsOrLikes(videoIds, 0), executor);

        // 查询作者信息
//        R<List<UserPo>> authorsR = userFeignService.getAvatarsAndNicknamesOfUsers(authorIds);
//
//        Map<Long, UserPo> authorsMap = null;
//        if (authorsR.getCode() == 200) {
//            List<UserPo> authors = authorsR.getDataOfJsonObject(new TypeReference<List<UserPo>>() {
//            });
//            authorsMap = authors.stream().collect(Collectors.toMap(UserPo::getId, userPo -> userPo));
//        } else {
//            authorsMap = new HashMap<>();
//        }
//        Map<Long, Long> videoViewsMap = videoViewsMapFuture.get();
//        Map<Long, UserPo> finalAuthorsMap = authorsMap;
        // 查询作者信息
        List<UserPo> authors = userApiService.getAvatarsAndNicknamesOfUsers(authorIds);
        final Map<Long, UserPo> authorsMap = authors.stream().collect(Collectors.toMap(UserPo::getId, userPo -> userPo));
        final Map<Long, Long> videoViewsMap = videoViewsMapFuture.get();
        return videos.stream().map(videoPo -> {
            UserPo author = authorsMap.get(videoPo.getUserId());
            String authorNickname = null;
            String authorAvatar = null;
            if (author == null) {
                authorNickname = "用户不存在";
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
                    videoViewsMap.get(videoPo.getId()),
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
        long[] videoIds = new long[videos.size()];
        for (int i = 0; i < videoIds.length; i++) {
            videoIds[i] = videos.get(i).getId();
        }
        // 查询每个视频的播放量
        Map<Long, Long> videoViewsMap = this.multiGetViewsOrLikes(videoIds, 0);
        List<PageVideoVo> pageVideos = new ArrayList<>(videos.size());

        for (VideoPo video : videos) {
            PageVideoVo pageVideoVo = new PageVideoVo(video.getId(),
                    video.getCoverUrl(),
                    video.getVideoUrl(),
                    video.getTitle(),
                    video.getType(),
                    videoViewsMap.get(video.getId()),
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

    @Override
    public long[] listIdByIndexAndSize(long index, long size) {
        return videoDao.listIdByIndexAndSize(index, size);
    }

    @Override
    public Long getVideoViewsOrLikes(Long videoId, Integer type) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        String key = null;
        if (type == 0) {
            key = VideoConstant.VIDEO_VIEWS_REDIS_KEY + videoId;
        } else {
            key = VideoConstant.VIDEO_LIKES_REDIS_KEY + videoId;
        }

        Object viewsOrLikes = opsForValue.get(key);
        if (viewsOrLikes == null) {
            // 使用分布式锁
            RLock lock = redissonClient.getLock("getVideoViewsOrLikes:" + videoId);
            lock.lock(5L, TimeUnit.SECONDS);

            Object getViewsOrLikesFromCacheAgain = opsForValue.get(key);
            if (getViewsOrLikesFromCacheAgain != null) {
                lock.unlock();
                viewsOrLikes = getViewsOrLikesFromCacheAgain;
            } else {
                if (type == 0) {
                    VideoPo video = this.getOne(new QueryWrapper<VideoPo>()
                            .select("views")
                            .eq("id", videoId));
                    if (video == null) {
                        lock.unlock();
                        throw new MyTopException(ExceptionContent.DIY_EXCEPTION.getCode(), "你在干什么?");
                    }
                    viewsOrLikes = video.getViews();
                } else {
                    VideoPo video = this.getOne(new QueryWrapper<VideoPo>()
                            .select("likes")
                            .eq("id", videoId));
                    if (video == null) {
                        lock.unlock();
                        throw new MyTopException(ExceptionContent.DIY_EXCEPTION.getCode(), "你在干什么?");
                    }
                    viewsOrLikes = video.getLikes();
                }
                opsForValue.set(key, viewsOrLikes);
                lock.unlock();
            }
        }
        return Long.valueOf(String.valueOf(viewsOrLikes));
    }

    @Override
    public void videoViewsOrLikesIncrement(Long videoId, Integer type) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        if (type == 0) {
            opsForValue.increment(VideoConstant.VIDEO_VIEWS_REDIS_KEY + videoId, 1L);
        } else {
            opsForValue.increment(VideoConstant.VIDEO_LIKES_REDIS_KEY + videoId, 1L);
        }
    }
}

package icu.yujing.product.constant;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Cyqurt
 * @date: 2022/3/28
 * @version: 1.0
 * @description:
 */
public class VideoConstant {
    /**
     * Key: 分片上传的Id value:对应的本地文件的路径
     * 改进: value存储路径和时间,开一个定时任务,过N个小时扫描一次,如果时间距离扫描时间
     * N个小时就从sliceUploadingMap中删除掉 (避免内存溢出)
     */
    public static ConcurrentHashMap<String, String> sliceUploadingMap = new ConcurrentHashMap<>();

    /**
     * 单位MB
     */
    public static Long allowedVideoCoverFileSize = 10L;
    public static List<String> allowedVideoCoverFileType = Arrays.asList("jpg", "jpeg", "png");
    /**
     * 单位MB
     */
    public static Long allowedVideoSliceFileSize = 10L;
    public static List<String> allowedVideoFileType = Arrays.asList("mp4", "flv", "mkv");

//    public static List<String> allowedVideoOrderFields = Arrays.asList("uploading_date", "views");

    public static final String VIDEO_VIEWS_REDIS_KEY = "videos:views:";
    public static final String VIDEO_LIKES_REDIS_KEY = "videos:likes:";
    public static final String VIDEO_DETAILS_REDIS_KEY = "videos:details:";


}

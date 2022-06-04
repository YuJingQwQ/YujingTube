package icu.yujing.common.constant;

/**
 * @author: Cyqurt
 * @date: 2022/3/23
 * @version: 1.0
 * @description:
 */
public class ProductModuleConstant {
    public static final class VideoConstant {
        public static final int WAITING_PASSING = 0;
        public static final int PASSED_WAITING_RELEASING = 1;
        public static final int RELEASED = 2;
        public static final int UNPASSING = 3;
        public static final int APPEALING = 4;

        public static final String VIDEO_VIEWS_REDIS_KEY = "videos:views:";
        public static final String VIDEO_LIKES_REDIS_KEY = "videos:likes:";
        public static final String VIDEO_DETAILS_REDIS_KEY = "videos:details:";
    }
}

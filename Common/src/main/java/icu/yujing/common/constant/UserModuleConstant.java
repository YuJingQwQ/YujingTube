package icu.yujing.common.constant;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
public class UserModuleConstant {
    public static final String USER = "user";
    public static final String JWT_USER_SIGNATURE = "jwt-user-signature";
    public static final String JWT_USER_KEY = "jwt-user-key";
    public static final long JWT_USER_EXPIRATION = 1000 * 60 * 60 * 24 * 30L;
    public static final String JWT_TOKEN_HTTP_HEADER_KEY = "token";
    public static final String USER_KEY_PREFIX_IN_REDIS = "user:id:";
    public static final String USER_VERIFICATION_CODE_PREFIX = "user:verification-code:";
    public static final Long USER_AVATAR_FILE_SIZE_LIMIT = 10L;
    public static final List<String> USER_AVATAR_ALLOWED_FILE_TYPES = Arrays.asList("jpeg", "jpg", "png");
    public static final SimpleDateFormat OSS_TIME_FOMATTER = new SimpleDateFormat("yyyy-MM-dd");
    public static final String USER_AVATAR_OSS_PREFIX = "user/avatar/";
    public static final String USER_ARTICLE_OSS_PREFIX = "user/article/";
    public static final String USER_ARTICLE_LOCAL_PREFIX = "D:\\slice\\";
}

package icu.yujing.common.constant;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description: 代号
 * 00: common
 * 10: video模块
 * 20: user模块
 * 30: thirdparty模块
 * 90: admin模块
 */
public enum ExceptionContent {
    FILE_UPLOADING_ERROR(00000, "文件上传失败"),
    EMPTY_FILE(00001, "文件为空"),
    WRONG_FILE_SIZE(00002, "文件大小非法"),
    WRONG_FILE_TYPE(00003, "文件类型非法"),
    NO_ENOUGH_AUTHORITY(00004,"访问失败"),
    ACCOUNT_IS_NOT_EXISTED(20000, "用户不存在"),
    NO_SUCH_VIDEO(10000,"查询不到此视频信息"),
    ACCOUNT_IS_EXISTED(20001, "用户已存在"),
    NICKNAME_IS_EXISTED(20002, "用户名已存在"),
    PHONE_IS_EXISTED(20003, "手机号已存在"),
    DIFFERENT_PASSWORD(20004, "两次密码不一致"),
    WRONG_ACCOUNT_OR_PASSWORD(20005, "账号或密码错误"),
    LOGIN_HAS_EXPIRED(20006, "登录已失效"),
    REDIS_KEY_IS_EXISTED(30000, "RedisKey已存在"),
    REDIS_KEY_CANT_BE_EXPIRED(30001, "RedisKey不能被设置存活时间"),
    REDIS_KEY_CANT_BE_DELETED(30002, "RedisKey删除失败"),
    WRONG_VERIFICATION_CODE(30010, "验证码错误"),
    UNKNOWN_EXCEPTION(80000, "未知异常"),
    DIY_EXCEPTION(88888, "自定义异常"),
    VALIDATION_EXCEPTION(89999, "数据校验异常");


    private int code;
    private String message;

    ExceptionContent() {
    }

    ExceptionContent(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package icu.yujing.common.exception;

/**
 * @author: Cyqurt
 * @date: 2022/3/21
 * @version: 1.0
 * @description:
 */
public class MyTopException extends RuntimeException {
    private Integer code;

    public MyTopException() {
    }

    public MyTopException(int code) {
        this.code = code;
    }

    public MyTopException(String message) {
        super(message);
        this.code = 0000;
    }

    public MyTopException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

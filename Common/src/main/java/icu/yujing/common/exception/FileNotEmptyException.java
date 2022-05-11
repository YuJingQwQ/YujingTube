package icu.yujing.common.exception;

import icu.yujing.common.constant.ExceptionContent;

/**
 * @author: Cyqurt
 * @date: 2022/3/19
 * @version: 1.0
 * @description:
 */
public class FileNotEmptyException extends MyTopException {

    public FileNotEmptyException() {
        super(ExceptionContent.EMPTY_FILE.getCode(), ExceptionContent.EMPTY_FILE.getMessage());
    }

    public FileNotEmptyException(Integer code, String message) {
        super(code, message);
    }
}

package icu.yujing.common.exception;

import icu.yujing.common.constant.ExceptionContent;

/**
 * @author: Cyqurt
 * @date: 2022/3/21
 * @version: 1.0
 * @description:
 */
public class WrongFileSizeException extends MyTopException {

    public WrongFileSizeException() {
        super(ExceptionContent.WRONG_FILE_SIZE.getCode(), ExceptionContent.WRONG_FILE_SIZE.getMessage());
    }

    public WrongFileSizeException(String message) {
        super(ExceptionContent.WRONG_FILE_SIZE.getCode(), message);
    }
}

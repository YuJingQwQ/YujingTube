package icu.yujing.common.exception;

import icu.yujing.common.constant.ExceptionContent;

/**
 * @author: Cyqurt
 * @date: 2022/3/21
 * @version: 1.0
 * @description:
 */
public class WrongFileTypeException extends MyTopException {
    public WrongFileTypeException() {
        super(ExceptionContent.WRONG_FILE_TYPE.getCode(), ExceptionContent.WRONG_FILE_TYPE.getMessage());
    }

    public WrongFileTypeException(String message) {
        super(ExceptionContent.WRONG_FILE_TYPE.getCode(), message);
    }

    public WrongFileTypeException(int code, String message) {
        super(code, message);
    }


}

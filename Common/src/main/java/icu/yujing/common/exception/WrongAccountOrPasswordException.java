package icu.yujing.common.exception;

import icu.yujing.common.constant.ExceptionContent;

/**
 * @author: Cyqurt
 * @date: 2022/3/21
 * @version: 1.0
 * @description:
 */
public class WrongAccountOrPasswordException extends MyTopException {
    public WrongAccountOrPasswordException() {
        super(ExceptionContent.WRONG_ACCOUNT_OR_PASSWORD.getCode(), ExceptionContent.WRONG_ACCOUNT_OR_PASSWORD.getMessage());
    }

    public WrongAccountOrPasswordException(String message) {
        super(ExceptionContent.WRONG_ACCOUNT_OR_PASSWORD.getCode(), message);
    }
}

package icu.yujing.common.exception;

import icu.yujing.common.constant.ExceptionContent;

/**
 * @author: Cyqurt
 * @date: 2022/3/21
 * @version: 1.0
 * @description:
 */
public class WrongVerificationCodeException extends MyTopException {
    public WrongVerificationCodeException() {
        super(ExceptionContent.WRONG_VERIFICATION_CODE.getCode(), ExceptionContent.WRONG_VERIFICATION_CODE.getMessage());
    }

    public WrongVerificationCodeException(int code, String message) {
        super(code, message);
    }
}

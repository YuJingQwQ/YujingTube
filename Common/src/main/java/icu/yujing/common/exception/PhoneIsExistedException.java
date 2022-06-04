package icu.yujing.common.exception;

import icu.yujing.common.constant.ExceptionContent;

/**
 * @author: Cyqurt
 * @date: 2022/3/21
 * @version: 1.0
 * @description:
 */
public class PhoneIsExistedException extends MyTopException {
    public PhoneIsExistedException() {
        super(ExceptionContent.PHONE_IS_EXISTED.getCode(), ExceptionContent.PHONE_IS_EXISTED.getMessage());
    }

    public PhoneIsExistedException(int code, String message) {
        super(code, message);
    }
}

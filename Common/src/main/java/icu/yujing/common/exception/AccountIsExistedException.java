package icu.yujing.common.exception;

import icu.yujing.common.constant.ExceptionContent;

/**
 * @author: Cyqurt
 * @date: 2022/3/21
 * @version: 1.0
 * @description:
 */
public class AccountIsExistedException extends MyTopException {
    public AccountIsExistedException() {
        super(ExceptionContent.ACCOUNT_IS_EXISTED.getCode(), ExceptionContent.ACCOUNT_IS_EXISTED.getMessage());
    }

    public AccountIsExistedException(int code, String message) {
        super(code, message);
    }
}

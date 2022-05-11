package icu.yujing.common.exception;

import icu.yujing.common.constant.ExceptionContent;

/**
 * @author: Cyqurt
 * @date: 2022/3/21
 * @version: 1.0
 * @description:
 */
public class NicknameIsExistedException extends MyTopException {
    public NicknameIsExistedException() {
        super(ExceptionContent.NICKNAME_IS_EXISTED.getCode(), ExceptionContent.NICKNAME_IS_EXISTED.getMessage());
    }

    public NicknameIsExistedException(int code, String message) {
        super(code, message);
    }
}

package icu.yujing.user.exception;

import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.exception.MyTopException;
import icu.yujing.common.utils.R;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@RestControllerAdvice
public class UserGlobalExceptionHandler {
    @ExceptionHandler(MyTopException.class)
    public R handleMyTopException(MyTopException e) {
        return R.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public R handleValidationException(BindException e) {
        StringBuilder errorMessageBuilder = new StringBuilder(20);

        for (FieldError fieldError : e.getFieldErrors()) {
            errorMessageBuilder.append(fieldError.getField())
                    .append(":")
                    .append(fieldError.getDefaultMessage())
                    .append(";");
        }
        return R.error(ExceptionContent.VALIDATION_EXCEPTION.getCode(), errorMessageBuilder.toString());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMessageBuilder = new StringBuilder(50);
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMessageBuilder.append(fieldError.getField())
                    .append(":")
                    .append(fieldError.getDefaultMessage())
                    .append(";");
        }

        return R.error(ExceptionContent.VALIDATION_EXCEPTION.getCode(), errorMessageBuilder.toString());
    }


    @ExceptionHandler(Exception.class)
    public R handleUnknownException(Exception e) {
        return R.error(ExceptionContent.UNKNOWN_EXCEPTION.getCode(), e.getMessage());
    }

}

package icu.yujing.user.entity.to;

import lombok.Data;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Data
public class VerificationCodeTo {
    /**
     * 电话号码
     */
    private String phone;
    /**
     * 验证码
     */
    private String verificationCode;
}

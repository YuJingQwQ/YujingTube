package icu.yujing.product.app.product.entity.to;

import lombok.Data;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Data
public class VerificationCodeTo {
    private String phone;
    private String verificationCode;
}

package icu.yujing.product.app.product.entity.vo;

import lombok.Data;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Data
public class SmsResponseVo {
    private String request_id;
    private String status;
    private String reason;
}

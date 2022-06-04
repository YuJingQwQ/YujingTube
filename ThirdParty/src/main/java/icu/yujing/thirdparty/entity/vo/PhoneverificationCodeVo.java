package icu.yujing.thirdparty.entity.vo;

import lombok.Data;

/**
 * @author: Cyqurt
 * @date: 2022/3/16
 * @version: 1.0
 * @description:
 */
@Data
public class PhoneverificationCodeVo {
    private String request_id;

    private String status;

    private String reason;

}

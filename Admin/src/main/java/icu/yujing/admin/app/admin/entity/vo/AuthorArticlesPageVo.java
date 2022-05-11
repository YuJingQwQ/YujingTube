package icu.yujing.admin.app.admin.entity.vo;

import lombok.Data;

/**
 * @author: Cyqurt
 * @date: 2022/3/27
 * @version: 1.0
 * @description:
 */
@Data
public class AuthorArticlesPageVo {
    private Long page;
    private Integer status;
    private Long zoneId;
    private String orderField;
    private Integer orderType;
}

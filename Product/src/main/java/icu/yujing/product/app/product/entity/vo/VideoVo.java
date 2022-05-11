package icu.yujing.product.app.product.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: Cyqurt
 * @date: 2022/3/19
 * @version: 1.0
 * @description:
 */
@Data
public class VideoVo {

    private Long id;

    private Long userId;

    private Integer status;

    private String title;

    private Integer type;

    private Long zoneId;

    private Long views;

    private Date releasingDate;

    private String coverUrl;

    private String videoUrl;

    private String description;

    private String authorAvatar;

    private String authorName;

}

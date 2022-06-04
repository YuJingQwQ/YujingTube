package icu.yujing.product.app.product.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: Cyqurt
 * @date: 2022/3/24
 * @version: 1.0
 * @description:
 */
@Data
public class CommentVo {
    private Long commentId;

    private Long videoId;

    private Long userId;

    private String username;

    private String userAvatar;

    private String comment;

    private Date date;

    private Integer ups;

    private Integer downs;

    private Integer myOperation;
}

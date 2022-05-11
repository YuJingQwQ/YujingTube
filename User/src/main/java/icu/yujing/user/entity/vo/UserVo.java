package icu.yujing.user.entity.vo;

import lombok.Data;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Data
public class UserVo {
    private Long id;
    private String nickname;
    private String avatar;
    private String account;
    private String password;
    private String phone;
    private Integer level;
    private Integer subscribingCount;
    private Integer fansCount;
    private Integer coinCount;
    private Integer videos;
    private Integer likes;
    private Long views;
}

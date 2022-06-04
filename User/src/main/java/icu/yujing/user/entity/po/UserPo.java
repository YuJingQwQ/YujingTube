package icu.yujing.user.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Data
@TableName("user_info")
public class UserPo implements Serializable {
    @TableId
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
    @TableLogic
    private Integer isDeleted;

}

package icu.yujing.product.app.product.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Data
@TableName("product_video")
public class VideoPo {
    @TableId
    private Long id;
    private String elasticsearchId;
    private Long userId;
    private Integer status;
    private String title;
    private Integer type;
    private Long zoneId;
    private String coverUrl;
    private String videoUrl;
    private String description;
    private Date uploadingDate;
    private Date releasingDate;
    private Long views;
    private Integer comments;
    private Long likes;

    @TableLogic
    private Integer isDeleted;
}
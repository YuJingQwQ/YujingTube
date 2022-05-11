package icu.yujing.product.app.product.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: Cyqurt
 * @date: 2022/3/24
 * @version: 1.0
 * @description:
 */
@Data
@TableName("product_comment")
public class CommentPo {

    @TableId
    private Long commentId;

    private Long videoId;

    private Long userId;

    private String comment;

    private Date date;

    private Integer ups;

    private Integer downs;

    @TableLogic
    private Integer isDeleted;

}

package icu.yujing.product.app.product.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: Cyqurt
 * @date: 2022/3/24
 * @version: 1.0
 * @description:
 */
@Data
@TableName("product_comment_operation")
public class CommentOperationPo {

    @TableId
    private Long commentId;

    private Long userId;

    private Long videoId;

    private Integer operation;

    /**
     * 不要添加@TableLogic标签
     */
    private Integer isDeleted;
}

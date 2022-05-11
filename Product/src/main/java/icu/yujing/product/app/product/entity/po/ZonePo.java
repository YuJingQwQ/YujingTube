package icu.yujing.product.app.product.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: Cyqurt
 * @date: 2022/3/19
 * @version: 1.0
 * @description:
 */
@Data
@TableName("product_zone")
public class ZonePo {
    @TableId
    private Long id;

    private String label;

    @TableLogic
    private Integer isDeleted;
}

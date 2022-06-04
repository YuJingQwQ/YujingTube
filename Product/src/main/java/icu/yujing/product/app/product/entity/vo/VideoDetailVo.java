package icu.yujing.product.app.product.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Data
public class VideoDetailVo {
    private PageVideoVo video;
    private List<CommentVo> comments;
}

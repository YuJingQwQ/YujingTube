package icu.yujing.product.app.product.entity.vo;

import icu.yujing.product.validation.group.VideoDetailsGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author: Cyqurt
 * @date: 2022/3/27
 * @version: 1.0
 * @description:
 */
@Data
public class UploadArticleDetailsVo {

    @Length(min = 1, max = 80, message = "{videoDetails.title.invalidLength}", groups = VideoDetailsGroup.class)
    @NotBlank(message = "{videoDetails.title.notBlank}", groups = VideoDetailsGroup.class)
    private String title;

    @Range(min = 0, max = 1, message = "{videoDetails.type.invalidValue}", groups = VideoDetailsGroup.class)
    @NotNull(message = "{videoDetails.type.notNull}", groups = VideoDetailsGroup.class)
    private Integer type;

    @NotNull(message = "{videoDetails.zoneId.notNull}", groups = VideoDetailsGroup.class)
    private Long zoneId;

    @NotBlank(message = "{videoDetails.coverUrl.notBlank}", groups = VideoDetailsGroup.class)
    @Pattern(regexp = "^https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/article/.*", message = "{videoDetails.coverUrl.invalid}", groups = VideoDetailsGroup.class)
    private String coverUrl;

    @NotBlank(message = "{videoDetails.videoUrl.notBlank}", groups = VideoDetailsGroup.class)
    @Pattern(regexp = "^https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/article/.*", message = "{videoDetails.videoUrl.invalid}", groups = VideoDetailsGroup.class)
    private String videoUrl;

    @Length(max = 2000, message = "{videoDetails.description.invalidLength}", groups = VideoDetailsGroup.class)
    private String description;

}

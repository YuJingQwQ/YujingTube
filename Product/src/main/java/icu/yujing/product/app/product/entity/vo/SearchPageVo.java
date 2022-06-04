package icu.yujing.product.app.product.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: Cyqurt
 * @date: 2022/3/27
 * @version: 1.0
 * @description:
 */
@Data
public class SearchPageVo {
    public Long videoId;
    private String videoCover;
    private String videoTitle;
    private String videoDescription;
    private Long videoViews;
    private Date releasingDate;
    private String authorName;
    private String authorAvatar;

    public SearchPageVo() {
    }

    public SearchPageVo(Long videoId, String videoCover, String videoTitle, String videoDescription, Long videoViews, Date releasingDate, String authorName, String authorAvatar) {
        this.videoId = videoId;
        this.videoCover = videoCover;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoViews = videoViews;
        this.releasingDate = releasingDate;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
    }
}

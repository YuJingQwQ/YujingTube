package icu.yujing.product.app.product.entity.to;

import lombok.Data;

import java.util.Date;

/**
 * @author: Cyqurt
 * @date: 2022/3/27
 * @version: 1.0
 * @description:
 */
@Data
public class ElasticSearchVideoTo {
    private Long videoId;
    private String videoCover;
    private String videoTitle;
    private String videoDescription;
    private Long videoViews;
    private Date releasingDate;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private Long zoneId;

    public ElasticSearchVideoTo() {
    }

    public ElasticSearchVideoTo(Long videoId, String videoTitle, Long zoneId, Long authorId) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.zoneId = zoneId;
        this.authorId = authorId;
    }

    public ElasticSearchVideoTo(Long videoId, String videoCover, String videoTitle, String videoDescription, Date releasingDate, Long authorId, String authorName, String authorAvatar, Long zoneId) {
        this.videoId = videoId;
        this.videoCover = videoCover;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.releasingDate = releasingDate;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.zoneId = zoneId;
    }
}

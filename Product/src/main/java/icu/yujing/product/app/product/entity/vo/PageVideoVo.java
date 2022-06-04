package icu.yujing.product.app.product.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: Cyqurt
 * @date: 2022/3/21
 * @version: 1.0
 * @description: 主页面和搜索页面还有视频播放页面的VO
 */
@Data
public class PageVideoVo {
    private Long videoId;
    private String videoCoverUrl;
    private String videoUrl;
    private String videoTitle;
    private Integer videoType;
    private Long videoViews;
    private String videoDescription;
    private Date videoReleasingDate;
    private Long zoneId;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private Integer authorFansCount;

    public PageVideoVo() {
    }

    public PageVideoVo(Long videoId, String videoCoverUrl, String videoUrl, String videoTitle, Integer videoType, Long videoViews, String videoDescription, Date videoReleasingDate, Long zoneId, Long authorId, String authorName, String authorAvatar) {
        this.videoId = videoId;
        this.videoCoverUrl = videoCoverUrl;
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
        this.videoType = videoType;
        this.videoViews = videoViews;
        this.videoDescription = videoDescription;
        this.videoReleasingDate = videoReleasingDate;
        this.zoneId = zoneId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
    }

    public PageVideoVo(Long videoId, String videoCoverUrl, String videoUrl, String videoTitle, Integer videoType, String videoDescription, Date videoReleasingDate, Long zoneId, Long authorId, String authorName, String authorAvatar, Integer authorFansCount) {
        this.videoId = videoId;
        this.videoCoverUrl = videoCoverUrl;
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
        this.videoType = videoType;
        this.videoDescription = videoDescription;
        this.videoReleasingDate = videoReleasingDate;
        this.zoneId = zoneId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.authorFansCount = authorFansCount;
    }
}

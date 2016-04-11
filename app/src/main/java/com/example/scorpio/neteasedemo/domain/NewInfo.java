package com.example.scorpio.neteasedemo.domain;

/**
 * Created by Scorpio on 16/2/13.
 */

/*新闻信息实体类*/
public class NewInfo {
    
    private String title;//标题
    private String detail;//详细
    private Integer comment;//跟帖数量
    private String imageUrl;//图片链接

    public NewInfo() {
        super();
    }

    @Override
    public String toString() {
        return "NewInfo{" +
                "title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", comment=" + comment +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public NewInfo(String title, String detail, Integer comment, String imageUrl) {
        super();
        this.title = title;
        this.detail = detail;
        this.comment = comment;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
        
    }

    public NewInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDetail() {
        return detail;
    }

    public NewInfo setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public Integer getComment() {
        return comment;
    }

    public NewInfo setComment(Integer comment) {
        this.comment = comment;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public NewInfo setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
}

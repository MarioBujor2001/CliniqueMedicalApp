package com.example.licenta.Models;

import java.io.Serializable;

public class ForumPost implements Serializable {
    private int id;
    private String imgUrl;
    private String title;
    private String content;

    public ForumPost(int id, String imgUrl, String title, String content) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.title = title;
        this.content = content;
    }

    public ForumPost() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "DailyNews{" +
                "id=" + id +
                ", imgUrl='" + imgUrl + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

package com.hujing.nanchangnews.Gson.News;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by acer on 2017/4/14.
 */

public class NewsData implements Serializable{
    public String uniquekey;
    public String title;
    public String date;
    public String category;
    @SerializedName("author_name")
    public String author;
    public String url;
    @SerializedName("thumbnail_pic_s")
    public String pic1;
    @SerializedName("thumbnail_pic_s02")
    public String pic2;
    @SerializedName("thumbnail_pic_s03")
    public String pic3;

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    @Override
    public String toString() {
        return "NewsData{" +
                "uniquekey='" + uniquekey + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", url='" + url + '\'' +
                ", pic1='" + pic1 + '\'' +
                ", pic2='" + pic2 + '\'' +
                ", pic3='" + pic3 + '\'' +
                '}';
    }
}

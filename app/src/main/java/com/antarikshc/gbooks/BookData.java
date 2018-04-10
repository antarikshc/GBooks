package com.antarikshc.gbooks;

import android.graphics.Bitmap;

public class BookData {

    private String title;
    private String author;
    private String desc;
    private Double price;
    private String publisher;
    private String previewUrl;
    private String imgUrl;
    private String buyUrl;
    private String currency;
    private Bitmap coverImage;
    private String buyString;

    BookData(String title, String author, String desc, Double price, String publisher, String previewUrl, String imgUrl, String buyUrl, String currency, Bitmap coverImage, String buyString) {
        this.title = title;
        this.author = author;
        this.desc = desc;
        this.price = price;
        this.publisher = publisher;
        this.previewUrl = previewUrl;
        this.imgUrl = imgUrl;
        this.buyUrl = buyUrl;
        this.currency = currency;
        this.coverImage = coverImage;
        this.buyString = buyString;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String infoUrl) {
        this.previewUrl = previewUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBuyUrl() {
        return buyUrl;
    }

    public void setBuyUrl(String buyUrl) {
        this.buyUrl = buyUrl;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Bitmap getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Bitmap coverImage) {
        this.coverImage = coverImage;
    }

    public String getBuyString() {
        return buyString;
    }

    public void setBuyString(String buyString) {
        this.buyString = buyString;
    }
}

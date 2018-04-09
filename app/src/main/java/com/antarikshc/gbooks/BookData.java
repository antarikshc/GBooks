package com.antarikshc.gbooks;

public class BookData {

    private String title;
    private String author;
    private String desc;
    private Double price;
    private String publisher;
    private String infoUrl;
    private String imgUrl;
    private String buyUrl;

    public BookData(String title, String author, String desc, Double price, String publisher, String infoUrl, String imgUrl, String buyUrl) {
        this.title = title;
        this.author = author;
        this.desc = desc;
        this.price = price;
        this.publisher = publisher;
        this.infoUrl = infoUrl;
        this.imgUrl = imgUrl;
        this.buyUrl = buyUrl;
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

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
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
}

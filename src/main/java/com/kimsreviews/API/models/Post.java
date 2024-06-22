package com.kimsreviews.API.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 255)
    private String title;

//    @Column(name = "content", length = 1000)
//    private String content;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "likes")
    private int likes;

    @Column(name = "views")
    private int views;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "poultry_type")
    private String poultryType;

    @Column(name = "weight")
    private String weight;

    @Column(name = "livestock_type")
    private String livestockType;

    @Column(name = "livestock_description", length = 1000)
    private String livestockDescription;

    @Column(name = "age")
    private String age;

    @Column(name = "sales_amount")
    private String salesAmount;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getPoultryType() {
        return poultryType;
    }

    public void setPoultryType(String poultryType) {
        this.poultryType = poultryType;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLivestockType() {
        return livestockType;
    }

    public void setLivestockType(String livestockType) {
        this.livestockType = livestockType;
    }

    public String getLivestockDescription() {
        return livestockDescription;
    }

    public void setLivestockDescription(String livestockDescription) {
        this.livestockDescription = livestockDescription;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(String salesAmount) {
        this.salesAmount = salesAmount;
    }
}

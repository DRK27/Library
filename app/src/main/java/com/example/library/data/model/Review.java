package com.example.library.data.model;

import java.util.Date;

public class Review {
    private String id;
    private String userId;
    private String bookId;
    private String userName;
    private String text;
    private double rating;
    private Date timestamp;

    public Review() {
    }

    public Review(String userId, String bookId, String userName, String text, double rating) {
        this.userId = userId;
        this.bookId = bookId;
        this.userName = userName;
        this.text = text;
        this.rating = rating;
        this.timestamp = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
} 
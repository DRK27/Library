package com.example.library.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Book implements Serializable {
    private String id;
    private String title;
    private String author;
    private String genre;
    private int year;
    private String description;
    private String coverImageUrl;
    private String qrCode;
    private String status;
    private String currentBorrowerId;
    private long dueDate;
    private double rating;
    private int ratingCount;
    private List<Review> reviews;
    private String source; // "firebase" or "openlibrary"
    private int totalCopies;
    private int availableCopies;

    public enum BookStatus {
        AVAILABLE,
        RESERVED,
        BORROWED
    }

    public Book() {
        // Required empty constructor for Firestore
        this.reviews = new ArrayList<>();
        this.status = "AVAILABLE";
        this.rating = 0.0;
        this.ratingCount = 0;
    }

    public Book(String title, String author, String genre, int year, String description, String coverImageUrl, int totalCopies) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.rating = 0.0;
        this.status = "AVAILABLE";
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.reviews = new ArrayList<>();
        this.ratingCount = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentBorrowerId() {
        return currentBorrowerId;
    }

    public void setCurrentBorrowerId(String currentBorrowerId) {
        this.currentBorrowerId = currentBorrowerId;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
        this.reviews.add(review);
        
        // Update average rating
        double totalRating = 0;
        for (Review r : this.reviews) {
            totalRating += r.getRating();
        }
        this.rating = totalRating / this.reviews.size();
        this.ratingCount = this.reviews.size();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFromOpenLibrary() {
        return "openlibrary".equals(source);
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
} 
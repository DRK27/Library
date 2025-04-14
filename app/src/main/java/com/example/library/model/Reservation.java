package com.example.library.model;

import java.util.Date;

public class Reservation {
    private String id;
    private String userId;
    private String bookId;
    private String bookTitle;
    private Date reservationDate;
    private Date expiryDate;
    private String status;

    public enum ReservationStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    public Reservation() {
    }

    public Reservation(String userId, String bookId, String bookTitle, Date reservationDate, Date expiryDate, String status) {
        this.userId = userId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.reservationDate = reservationDate;
        this.expiryDate = expiryDate;
        this.status = status;
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

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isExpired() {
        return new Date().after(expiryDate);
    }

    public boolean isActive() {
        return ReservationStatus.ACTIVE.name().equals(status);
    }

    public boolean isCompleted() {
        return ReservationStatus.COMPLETED.name().equals(status);
    }

    public boolean isCancelled() {
        return ReservationStatus.CANCELLED.name().equals(status);
    }
} 
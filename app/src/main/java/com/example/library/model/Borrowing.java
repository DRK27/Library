package com.example.library.model;

import java.util.Date;

public class Borrowing {
    private String id;
    private String bookId;
    private String userId;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private BorrowingStatus status;
    private boolean quizCompleted;
    private int quizScore;

    public enum BorrowingStatus {
        ACTIVE,
        RETURNED,
        OVERDUE
    }

    public Borrowing(String userId, String bookId) {
        this.borrowDate = new Date();
        this.status = BorrowingStatus.ACTIVE;
        this.quizCompleted = false;
        this.quizScore = 0;
    }

    public Borrowing(String id, String bookId, String userId) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = new Date();
        this.status = BorrowingStatus.ACTIVE;
        this.quizCompleted = false;
        this.quizScore = 0;
        
        // Set due date to 14 days from now
        this.dueDate = new Date(System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public BorrowingStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowingStatus status) {
        this.status = status;
    }

    public boolean isQuizCompleted() {
        return quizCompleted;
    }

    public void setQuizCompleted(boolean quizCompleted) {
        this.quizCompleted = quizCompleted;
    }

    public int getQuizScore() {
        return quizScore;
    }

    public void setQuizScore(int quizScore) {
        this.quizScore = quizScore;
    }

    // Helper methods
    public boolean isOverdue() {
        return new Date().after(dueDate) && status == BorrowingStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == BorrowingStatus.ACTIVE;
    }

    public boolean isReturned() {
        return status == BorrowingStatus.RETURNED;
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        
        long diff = new Date().getTime() - dueDate.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }

    public void returnBook() {
        this.returnDate = new Date();
        this.status = BorrowingStatus.RETURNED;
    }
} 
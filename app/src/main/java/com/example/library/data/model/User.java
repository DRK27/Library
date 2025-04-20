package com.example.library.data.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String name;
    private String email;
    private String photoUrl;
    private String memberSince;
    private int booksBorrowed;
    private int booksReserved;
    private int quizzesTaken;
    private double averageScore;
    private UserRole role;
    private List<String> borrowedBookIds;
    private List<String> reservedBookIds;
    private List<String> favoriteBookIds;
    private List<String> readBookIds;

    public enum UserRole {
        USER,
        ADMIN
    }

    public User() {
        // Required empty constructor for Firestore
        this.borrowedBookIds = new ArrayList<>();
        this.reservedBookIds = new ArrayList<>();
        this.favoriteBookIds = new ArrayList<>();
        this.readBookIds = new ArrayList<>();
        this.role = UserRole.USER;
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.memberSince = java.time.LocalDate.now().toString();
        this.booksBorrowed = 0;
        this.booksReserved = 0;
        this.quizzesTaken = 0;
        this.averageScore = 0.0;
        this.role = UserRole.USER;
        this.borrowedBookIds = new ArrayList<>();
        this.reservedBookIds = new ArrayList<>();
        this.favoriteBookIds = new ArrayList<>();
        this.readBookIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }

    public int getBooksBorrowed() {
        return booksBorrowed;
    }

    public void setBooksBorrowed(int booksBorrowed) {
        this.booksBorrowed = booksBorrowed;
    }

    public int getBooksReserved() {
        return booksReserved;
    }

    public void setBooksReserved(int booksReserved) {
        this.booksReserved = booksReserved;
    }

    public int getQuizzesTaken() {
        return quizzesTaken;
    }

    public void setQuizzesTaken(int quizzesTaken) {
        this.quizzesTaken = quizzesTaken;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<String> getBorrowedBookIds() {
        return borrowedBookIds;
    }

    public void setBorrowedBookIds(List<String> borrowedBookIds) {
        this.borrowedBookIds = borrowedBookIds;
    }

    public List<String> getReservedBookIds() {
        return reservedBookIds;
    }

    public void setReservedBookIds(List<String> reservedBookIds) {
        this.reservedBookIds = reservedBookIds;
    }

    public List<String> getFavoriteBookIds() {
        return favoriteBookIds;
    }

    public void setFavoriteBookIds(List<String> favoriteBookIds) {
        this.favoriteBookIds = favoriteBookIds;
    }

    public List<String> getReadBookIds() {
        return readBookIds;
    }

    public void setReadBookIds(List<String> readBookIds) {
        this.readBookIds = readBookIds;
    }

    // Helper methods
    public void addBorrowedBook(String bookId) {
        if (!borrowedBookIds.contains(bookId)) {
            borrowedBookIds.add(bookId);
        }
    }

    public void removeBorrowedBook(String bookId) {
        borrowedBookIds.remove(bookId);
    }

    public void addReservedBook(String bookId) {
        if (!reservedBookIds.contains(bookId)) {
            reservedBookIds.add(bookId);
        }
    }

    public void removeReservedBook(String bookId) {
        reservedBookIds.remove(bookId);
    }

    public void addFavoriteBook(String bookId) {
        if (!favoriteBookIds.contains(bookId)) {
            favoriteBookIds.add(bookId);
        }
    }

    public void removeFavoriteBook(String bookId) {
        favoriteBookIds.remove(bookId);
    }

    public void addReadBook(String bookId) {
        if (!readBookIds.contains(bookId)) {
            readBookIds.add(bookId);
        }
    }

    public boolean isLibrarian() {
        return role == UserRole.ADMIN;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
} 
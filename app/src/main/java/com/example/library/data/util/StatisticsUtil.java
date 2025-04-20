package com.example.library.data.util;

import com.example.library.data.model.Book;
import com.example.library.data.model.Borrowing;
import com.example.library.data.model.User;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class StatisticsUtil {
    

    public static double calculateAverageRating(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return 0.0;
        }
        
        double totalRating = 0.0;
        int ratedBooks = 0;
        
        for (Book book : books) {
            if (book.getRatingCount() > 0) {
                totalRating += book.getRating();
                ratedBooks++;
            }
        }
        
        return ratedBooks > 0 ? totalRating / ratedBooks : 0.0;
    }
    

    public static List<Book> getMostPopularBooks(List<Book> books, List<Borrowing> borrowings, int limit) {
        if (books == null || books.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Calculate borrowing frequency for each book
        Map<String, Integer> borrowingFrequency = new HashMap<>();
        if (borrowings != null) {
            for (Borrowing borrowing : borrowings) {
                String bookId = borrowing.getBookId();
                borrowingFrequency.put(bookId, borrowingFrequency.getOrDefault(bookId, 0) + 1);
            }
        }
        
        List<Map.Entry<Book, Double>> bookScores = new ArrayList<>();
        
        for (Book book : books) {
            double ratingScore = book.getRating() * book.getRatingCount();
            int borrowingCount = borrowingFrequency.getOrDefault(book.getId(), 0);
            double popularityScore = (ratingScore + borrowingCount) / 2.0;
            
            bookScores.add(new AbstractMap.SimpleEntry<>(book, popularityScore));
        }
        
        Collections.sort(bookScores, (o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
        
        return bookScores.stream()
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    

    public static Map<String, Double> getMostPopularGenres(List<Book> books, List<Borrowing> borrowings, int limit) {
        if (books == null || books.isEmpty()) {
            return new HashMap<>();
        }
        
        Map<String, Double> genreScores = new HashMap<>();
        Map<String, Integer> genreCounts = new HashMap<>();
        
        for (Book book : books) {
            String genre = book.getGenre();
            double ratingScore = book.getRating() * book.getRatingCount();
            
            genreScores.put(genre, genreScores.getOrDefault(genre, 0.0) + ratingScore);
            genreCounts.put(genre, genreCounts.getOrDefault(genre, 0) + 1);
        }
        
        Map<String, Double> averageGenreScores = new HashMap<>();
        for (String genre : genreScores.keySet()) {
            double averageScore = genreScores.get(genre) / genreCounts.get(genre);
            averageGenreScores.put(genre, averageScore);
        }
        
        return averageGenreScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        HashMap::new
                ));
    }
    

    public static List<User> getUsersWithOverdueBooks(List<User> users, List<Borrowing> borrowings) {
        if (users == null || users.isEmpty() || borrowings == null || borrowings.isEmpty()) {
            return new ArrayList<>();
        }
        
        Map<String, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }
        
        Set<String> overdueUserIds = new HashSet<>();
        Date now = new Date();
        
        for (Borrowing borrowing : borrowings) {
            if (borrowing.isOverdue()) {
                overdueUserIds.add(borrowing.getUserId());
            }
        }
        
        return overdueUserIds.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Book> getInactiveBooks(List<Book> books, List<Borrowing> borrowings, int days) {
        if (books == null || books.isEmpty()) {
            return new ArrayList<>();
        }
        
        Date cutoffDate = DateUtil.addDays(new Date(), -days);
        
        Map<String, Date> lastBorrowingDates = new HashMap<>();
        if (borrowings != null) {
            for (Borrowing borrowing : borrowings) {
                String bookId = borrowing.getBookId();
                Date borrowDate = borrowing.getBorrowDate();
                
                if (!lastBorrowingDates.containsKey(bookId) ||
                        borrowDate.after(lastBorrowingDates.get(bookId))) {
                    lastBorrowingDates.put(bookId, borrowDate);
                }
            }
        }
        
        return books.stream()
                .filter(book -> {
                    Date lastBorrowDate = lastBorrowingDates.get(book.getId());
                    return lastBorrowDate == null || lastBorrowDate.before(cutoffDate);
                })
                .collect(Collectors.toList());
    }
    

    public static Map<String, Object> calculateUserStatistics(User user, List<Borrowing> borrowings) {
        Map<String, Object> statistics = new HashMap<>();
        
        if (user == null || borrowings == null || borrowings.isEmpty()) {
            return statistics;
        }
        
        List<Borrowing> userBorrowings = borrowings.stream()
                .filter(b -> b.getUserId().equals(user.getId()))
                .collect(Collectors.toList());
        
        int totalBorrowings = userBorrowings.size();
        long overdueBorrowings = userBorrowings.stream()
                .filter(Borrowing::isOverdue)
                .count();
        long returnedOnTime = userBorrowings.stream()
                .filter(b -> b.isReturned() && !b.isOverdue())
                .count();
        double overdueRate = totalBorrowings > 0 ? (double) overdueBorrowings / totalBorrowings : 0.0;
        
        // Add statistics to map
        statistics.put("totalBorrowings", totalBorrowings);
        statistics.put("overdueBorrowings", overdueBorrowings);
        statistics.put("returnedOnTime", returnedOnTime);
        statistics.put("overdueRate", overdueRate);
        
        return statistics;
    }
    

    public static Map<String, Object> calculateLibraryStatistics(List<Book> books, List<User> users, List<Borrowing> borrowings) {
        Map<String, Object> statistics = new HashMap<>();
        
        if (books == null || users == null || borrowings == null) {
            return statistics;
        }
        
        // Basic counts
        statistics.put("totalBooks", books.size());
        statistics.put("totalUsers", users.size());
        statistics.put("totalBorrowings", borrowings.size());
        
        long availableBooks = books.stream()
                .filter(b -> b.getStatus().equals(Book.BookStatus.AVAILABLE.name()))
                .count();
        statistics.put("availableBooks", availableBooks);
        
        long activeBorrowings = borrowings.stream()
                .filter(Borrowing::isActive)
                .count();
        statistics.put("activeBorrowings", activeBorrowings);
        
        long overdueBorrowings = borrowings.stream()
                .filter(Borrowing::isOverdue)
                .count();
        statistics.put("overdueBorrowings", overdueBorrowings);
        
        double averageRating = calculateAverageRating(books);
        statistics.put("averageRating", averageRating);
        
        Map<String, Double> popularGenres = getMostPopularGenres(books, borrowings, 5);
        statistics.put("popularGenres", popularGenres);
        
        return statistics;
    }
} 
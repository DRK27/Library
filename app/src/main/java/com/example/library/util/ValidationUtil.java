package com.example.library.util;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && PASSWORD_PATTERN.matcher(password).matches();
    }


    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.matches("^[a-zA-Z\\s]+$");
    }
    

    public static boolean isValidBookTitle(String title) {
        return !TextUtils.isEmpty(title) && title.length() >= 2;
    }
    

    public static boolean isValidAuthorName(String author) {
        return !TextUtils.isEmpty(author) && author.matches("^[a-zA-Z\\s.-]+$");
    }

    public static boolean isValidPublicationYear(int year) {
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        return year >= 1000 && year <= currentYear;
    }
    

    public static boolean isValidISBN(String isbn) {
        if (TextUtils.isEmpty(isbn)) {
            return false;
        }
        
        isbn = isbn.replaceAll("[-\\s]", "");
        
        if (isbn.length() == 10) {
            return isValidISBN10(isbn);
        } else if (isbn.length() == 13) {
            return isValidISBN13(isbn);
        }
        
        return false;
    }

    private static boolean isValidISBN10(String isbn) {
        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                int digit = Character.getNumericValue(isbn.charAt(i));
                sum += (digit * (10 - i));
            }
            
            char last = isbn.charAt(9);
            if (last == 'X') {
                sum += 10;
            } else {
                sum += Character.getNumericValue(last);
            }
            
            return (sum % 11 == 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    

    private static boolean isValidISBN13(String isbn) {
        try {
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                int digit = Character.getNumericValue(isbn.charAt(i));
                sum += (i % 2 == 0) ? digit : digit * 3;
            }
            
            int checksum = 10 - (sum % 10);
            if (checksum == 10) {
                checksum = 0;
            }
            
            return checksum == Character.getNumericValue(isbn.charAt(12));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidRating(double rating) {
        return rating >= 1.0 && rating <= 5.0;
    }

    public static boolean isValidReview(String review) {
        return !TextUtils.isEmpty(review) && review.length() >= 10 && review.length() <= 1000;
    }

    public static boolean isValidQuizQuestion(String questionText, String[] options, int correctOptionIndex) {
        if (TextUtils.isEmpty(questionText) || options == null || options.length < 2) {
            return false;
        }
        
        for (String option : options) {
            if (TextUtils.isEmpty(option)) {
                return false;
            }
        }
        
        return correctOptionIndex >= 0 && correctOptionIndex < options.length;
    }
} 
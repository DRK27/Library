package com.example.library.data.model;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private String id;
    private String bookId;
    private String title;
    private List<Question> questions;

    public Quiz() {
        this.questions = new ArrayList<>();
    }

    public Quiz(String id, String bookId, String title) {
        this.id = id;
        this.bookId = bookId;
        this.title = title;
        this.questions = new ArrayList<>();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        if (this.questions == null) {
            this.questions = new ArrayList<>();
        }
        this.questions.add(question);
    }

    public static class Question {
        private String id;
        private String text;
        private List<String> options;
        private int correctOptionIndex;

        public Question() {
            // Default constructor required for Firestore
            this.options = new ArrayList<>();
        }

        public Question(String id, String text, List<String> options, int correctOptionIndex) {
            this.id = id;
            this.text = text;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public int getCorrectOptionIndex() {
            return correctOptionIndex;
        }

        public void setCorrectOptionIndex(int correctOptionIndex) {
            this.correctOptionIndex = correctOptionIndex;
        }

        public boolean isCorrect(int selectedOptionIndex) {
            return selectedOptionIndex == correctOptionIndex;
        }
    }
} 
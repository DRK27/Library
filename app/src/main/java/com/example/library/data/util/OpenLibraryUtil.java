package com.example.library.data.util;

import android.util.Log;

import com.example.library.data.model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OpenLibraryUtil {
    private static final String TAG = "OpenLibraryUtil";
    private static final String BASE_URL = "https://openlibrary.org";
    private static final String SEARCH_URL = BASE_URL + "/search.json?q=";
    private static final String BOOK_DETAILS_URL = BASE_URL + "/books/";
    private static final String COVER_URL = "https://covers.openlibrary.org/b/id/";
    
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public interface OnBooksLoadedListener {
        void onBooksLoaded(List<Book> books);
        void onError(String error);
    }
    
    public static void searchBooks(String query, OnBooksLoadedListener listener) {
        executor.execute(() -> {
            try {
                String encodedQuery = URLEncoder.encode(query, "UTF-8");
                String urlString = SEARCH_URL + encodedQuery;
                
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    List<Book> books = parseSearchResults(response.toString());
                    listener.onBooksLoaded(books);
                } else {
                    listener.onError("Error: " + responseCode);
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error searching books", e);
                listener.onError("Error: " + e.getMessage());
            }
        });
    }
    
    public static void getBookDetails(String key, OnBooksLoadedListener listener) {
        executor.execute(() -> {
            try {
                String urlString = BOOK_DETAILS_URL + key + ".json";
                
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    Book book = parseBookDetails(response.toString(), key);
                    List<Book> books = new ArrayList<>();
                    books.add(book);
                    listener.onBooksLoaded(books);
                } else {
                    listener.onError("Error: " + responseCode);
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error getting book details", e);
                listener.onError("Error: " + e.getMessage());
            }
        });
    }
    
    private static List<Book> parseSearchResults(String jsonString) throws JSONException {
        List<Book> books = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray docs = jsonObject.getJSONArray("docs");
        
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            Book book = new Book();
            
            book.setId(doc.optString("key", "").replace("/books/", ""));
            book.setTitle(doc.optString("title", "Unknown Title"));
            book.setAuthor(doc.optString("author_name", new JSONArray().getString(0)));
            book.setGenre(doc.optString("subject", new JSONArray().getString(0)));
            book.setDescription(doc.optString("first", "No description available"));
            
            if (doc.has("cover_i")) {
                int coverId = doc.getInt("cover_i");
                book.setCoverImageUrl(COVER_URL + coverId + "-L.jpg");
            }
            
            if (doc.has("ratings_average")) {
                book.setRating(doc.getDouble("ratings_average"));
            }
            
            book.setStatus(String.valueOf(Book.BookStatus.AVAILABLE));
            
            book.setSource("openlibrary");
            
            books.add(book);
        }
        
        return books;
    }
    
    private static Book parseBookDetails(String jsonString, String key) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        Book book = new Book();
        
        book.setId(key.replace("/books/", ""));
        book.setTitle(jsonObject.optString("title", "Unknown Title"));
        
        if (jsonObject.has("authors")) {
            JSONArray authors = jsonObject.getJSONArray("authors");
            if (authors.length() > 0) {
                JSONObject author = authors.getJSONObject(0);
                book.setAuthor(author.optString("name", "Unknown Author"));
            }
        }
        
        if (jsonObject.has("subjects")) {
            JSONArray subjects = jsonObject.getJSONArray("subjects");
            if (subjects.length() > 0) {
                book.setGenre(subjects.getString(0));
            }
        }
        
        book.setDescription(jsonObject.optString("description", "No description available"));
        
        if (jsonObject.has("covers")) {
            JSONArray covers = jsonObject.getJSONArray("covers");
            if (covers.length() > 0) {
                int coverId = covers.getInt(0);
                book.setCoverImageUrl(COVER_URL + coverId + "-L.jpg");
            }
        }
        
        book.setStatus(String.valueOf(Book.BookStatus.AVAILABLE));
        
        book.setSource("openlibrary");
        
        return book;
    }
} 
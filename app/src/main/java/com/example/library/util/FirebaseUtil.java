package com.example.library.util;

import android.net.Uri;
import android.util.Log;

import com.example.library.model.Book;
import com.example.library.model.Borrowing;
import com.example.library.model.Quiz;
import com.example.library.model.Reservation;
import com.example.library.model.Review;
import com.example.library.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUtil {
    private static final String TAG = "FirebaseUtil";

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    // Collection names
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_BOOKS = "books";
    private static final String COLLECTION_BORROWINGS = "borrowings";
    private static final String COLLECTION_RESERVATIONS = "reservations";
    private static final String COLLECTION_REVIEWS = "reviews";
    private static final String COLLECTION_QUIZZES = "quizzes";

    // Storage paths
    private static final String STORAGE_BOOK_COVERS = "book_covers";
    private static final String STORAGE_USER_PHOTOS = "user_photos";

    // User operations
    public static void createUser(User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_USERS)
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void getUser(String userId, OnSuccessListener<DocumentSnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void updateUser(User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_USERS)
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Book operations
    public static void createBook(Book book, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BOOKS)
                .add(book)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void getBook(String bookId, OnSuccessListener<DocumentSnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BOOKS)
                .document(bookId)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void updateBook(Book book, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BOOKS)
                .document(book.getId())
                .set(book)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void searchBooks(String query, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BOOKS)
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + '\uf8ff')
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void getBooksByGenre(String genre, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BOOKS)
                .whereEqualTo("genre", genre)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }


    public static void getBooksByStatus(Book.BookStatus status, OnSuccessListener<QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_BOOKS)
                .whereEqualTo("status", status)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    // Borrowing operations
    public static void createBorrowing(Borrowing borrowing, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BORROWINGS)
                .add(borrowing)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void getBorrowing(String borrowingId, OnSuccessListener<DocumentSnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BORROWINGS)
                .document(borrowingId)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void updateBorrowing(Borrowing borrowing, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BORROWINGS)
                .document(borrowing.getId())
                .set(borrowing)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void getBorrowingsByUser(String userId, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BORROWINGS)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void getActiveBorrowingsByUser(String userId, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BORROWINGS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", Borrowing.BorrowingStatus.ACTIVE)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void getOverdueBorrowings(OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        Date now = new Date();
        db.collection(COLLECTION_BORROWINGS)
                .whereEqualTo("status", Borrowing.BorrowingStatus.ACTIVE)
                .whereLessThan("dueDate", now)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Reservation operations

    public static void createReservation(Reservation reservation, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_RESERVATIONS)
                .add(reservation)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    public static void getReservation(String reservationId, OnSuccessListener<DocumentSnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_RESERVATIONS)
                .document(reservationId)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    public static void getReservationsByUser(String userId, OnSuccessListener<QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_RESERVATIONS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", Reservation.ReservationStatus.ACTIVE.name())
                .orderBy("reservationDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    public static void getActiveReservationsByBook(String bookId, OnSuccessListener<QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_RESERVATIONS)
                .whereEqualTo("bookId", bookId)
                .whereEqualTo("status", Reservation.ReservationStatus.ACTIVE)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
    public static void updateReservation(Reservation reservation, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_RESERVATIONS)
                .document(reservation.getId())
                .set(reservation)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    // Review operations
    public static void createReview(Review review, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_REVIEWS)
                .add(review)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void getReviewsByBook(String bookId, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_REVIEWS)
                .whereEqualTo("bookId", bookId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Quiz operations
    public static void createQuiz(Quiz quiz, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_QUIZZES)
                .add(quiz)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void getQuizByBook(String bookId, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_QUIZZES)
                .whereEqualTo("bookId", bookId)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Storage operations
    public static void uploadBookCover(Uri imageUri, String bookId, OnSuccessListener<UploadTask.TaskSnapshot> successListener, OnFailureListener failureListener) {
        StorageReference storageRef = storage.getReference().child(STORAGE_BOOK_COVERS).child(bookId);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void uploadUserPhoto(Uri imageUri, String userId, OnSuccessListener<UploadTask.TaskSnapshot> successListener, OnFailureListener failureListener) {
        StorageReference storageRef = storage.getReference().child(STORAGE_USER_PHOTOS).child(userId);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static String getBookCoverUrl(String bookId) {
        return storage.getReference().child(STORAGE_BOOK_COVERS).child(bookId).getDownloadUrl().toString();
    }

    public static String getUserPhotoUrl(String userId) {
        return storage.getReference().child(STORAGE_USER_PHOTOS).child(userId).getDownloadUrl().toString();
    }

    // Statistics operations
    public static void getPopularBooks(OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BOOKS)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public static void getPopularGenres(OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection(COLLECTION_BOOKS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Integer> genreCount = new HashMap<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        String genre = book.getGenre();

                        if (genreCount.containsKey(genre)) {
                            genreCount.put(genre, genreCount.get(genre) + 1);
                        } else {
                            genreCount.put(genre, 1);
                        }
                    }

                    // Convert to list and sort
                    List<Map.Entry<String, Integer>> sortedGenres = new ArrayList<>(genreCount.entrySet());
                    sortedGenres.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

                    // Create a list of top 5 genres
                    List<String> topGenres = new ArrayList<>();
                    for (int i = 0; i < Math.min(5, sortedGenres.size()); i++) {
                        topGenres.add(sortedGenres.get(i).getKey());
                    }

                    // Create a new query to get the top genres
                    Query query = db.collection(COLLECTION_BOOKS)
                            .whereIn("genre", topGenres)
                            .limit(5);

                    query.get()
                            .addOnSuccessListener(successListener)
                            .addOnFailureListener(failureListener);
                })
                .addOnFailureListener(failureListener);
    }

    public static void getUsersWithOverdueBooks(OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        Date now = new Date();
        db.collection(COLLECTION_BORROWINGS)
                .whereEqualTo("status", Borrowing.BorrowingStatus.ACTIVE)
                .whereLessThan("dueDate", now)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> userIds = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Borrowing borrowing = document.toObject(Borrowing.class);
                        userIds.add(borrowing.getUserId());
                    }

                    // Get unique user IDs
                    List<String> uniqueUserIds = new ArrayList<>();
                    for (String userId : userIds) {
                        if (!uniqueUserIds.contains(userId)) {
                            uniqueUserIds.add(userId);
                        }
                    }

                    // Get user documents
                    if (uniqueUserIds.isEmpty()) {
                        // Return empty result by querying a non-existent collection
                        db.collection("empty_collection")
                                .whereEqualTo("id", "non_existent")
                                .get()
                                .addOnSuccessListener(successListener)
                                .addOnFailureListener(failureListener);
                        return;
                    }

                    // Get user documents
                    db.collection(COLLECTION_USERS)
                            .whereIn("id", uniqueUserIds)
                            .get()
                            .addOnSuccessListener(successListener)
                            .addOnFailureListener(failureListener);
                })
                .addOnFailureListener(failureListener);
    }

    public static void addTestBook(OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        Book testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setGenre("Fiction");
        testBook.setStatus(Book.BookStatus.AVAILABLE.name());
        testBook.setDescription("This is a test book");
        testBook.setRating(4.5f);
        testBook.setCoverImageUrl("https://covers.openlibrary.org/b/id/1-L.jpg");

        db.collection(COLLECTION_BOOKS)
                .add(testBook)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Test book added with ID: " + documentReference.getId());
                    if (successListener != null) {
                        successListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding test book", e);
                    if (failureListener != null) {
                        failureListener.onFailure(e);
                    }
                });
    }


    public static void getBooks(OnSuccessListener<QuerySnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_BOOKS)
                .get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public static void addBook(Book book, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(COLLECTION_BOOKS)
                .add(book)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public static String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    public static void borrowBook(String bookId, String userId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.runTransaction(transaction -> {
                    DocumentReference bookRef = db.collection(COLLECTION_BOOKS).document(bookId);
                    DocumentSnapshot bookSnapshot = transaction.get(bookRef);

                    if (!bookSnapshot.exists()) {
                        throw new FirebaseFirestoreException("Book not found", null);
                    }

                    Book book = bookSnapshot.toObject(Book.class);
                    if (book == null || book.getAvailableCopies() <= 0) {
                        throw new FirebaseFirestoreException("Book is not available", null);
                    }

                    // Create borrowing
                    Borrowing borrowing = new Borrowing(userId, bookId);
                    DocumentReference borrowingRef = db.collection(COLLECTION_BORROWINGS).document();
                    transaction.set(borrowingRef, borrowing);

                    // Update book
                    book.setAvailableCopies(book.getAvailableCopies() - 1);
                    if (book.getAvailableCopies() == 0) {
                        book.setStatus(Book.BookStatus.BORROWED.name());
                    }
                    transaction.update(bookRef, (Map<String, Object>) book);

                    return null;
                })
                .addOnSuccessListener(aVoid -> {
                    if (successListener != null) {
                        successListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(failureListener);
    }

    public static void reserveBook(String bookId, String userId, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.runTransaction(transaction -> {
                    DocumentReference bookRef = db.collection(COLLECTION_BOOKS).document(bookId);
                    DocumentSnapshot bookSnapshot = transaction.get(bookRef);

                    if (!bookSnapshot.exists()) {
                        throw new FirebaseFirestoreException("Book not found", null);
                    }

                    Book book = bookSnapshot.toObject(Book.class);
                    if (book == null || book.getAvailableCopies() > 0) {
                        throw new FirebaseFirestoreException("Book is available, no need to reserve", null);
                    }

                    // Create reservation
                    Reservation reservation = new Reservation(userId, bookId, book.getTitle(), new Date(), new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000), "ACTIVE");
                    DocumentReference reservationRef = db.collection(COLLECTION_RESERVATIONS).document();
                    transaction.set(reservationRef, reservation);
                    return null;
                })
                .addOnSuccessListener(aVoid -> {
                    if (successListener != null) {
                        successListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(failureListener);
    }
}
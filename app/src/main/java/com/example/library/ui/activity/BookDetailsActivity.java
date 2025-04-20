//package com.example.library;
//
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RatingBar;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.library.adapter.ReviewAdapter;
//import com.example.library.model.Book;
//import com.example.library.model.Review;
//import com.example.library.util.FirebaseUtil;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BookDetailsActivity extends AppCompatActivity {
//
//    public static final String EXTRA_BOOK_ID = "book_id";
//
//    private ImageView bookCover;
//    private TextView bookTitle, bookAuthor, bookGenre, bookDescription, bookStatus, bookRating;
//    private MaterialButton actionButton;
//    private RecyclerView reviewsList;
//    private FloatingActionButton fabAddReview;
//    private ReviewAdapter reviewAdapter;
//
//    private Book book;
//    private String bookId;
//    private String userId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_book_details);
//
//        // Получаем ID книги из Intent
//        bookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
//        if (bookId == null) {
//            finish();
//            return;
//        }
//
//        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        initializeViews();
//        setupToolbar();
//        setupRecyclerView();
//        loadBookDetails();
//    }
//
//    private void initializeViews() {
//        bookCover = findViewById(R.id.book_cover);
//        bookTitle = findViewById(R.id.book_title);
//        bookAuthor = findViewById(R.id.book_author);
//        bookGenre = findViewById(R.id.book_genre);
//        bookDescription = findViewById(R.id.book_description);
//        bookStatus = findViewById(R.id.book_status);
//        bookRating = findViewById(R.id.book_rating);
//        actionButton = findViewById(R.id.action_button);
//        reviewsList = findViewById(R.id.reviews_list);
//        fabAddReview = findViewById(R.id.fab_add_review);
//
//        fabAddReview.setOnClickListener(v -> showAddReviewDialog());
//    }
//
//    private void setupToolbar() {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("");
//        }
//    }
//
//    private void setupRecyclerView() {
//        reviewAdapter = new ReviewAdapter(new ArrayList<>());
//        reviewsList.setLayoutManager(new LinearLayoutManager(this));
//        reviewsList.setAdapter(reviewAdapter);
//    }
//
//    private void loadBookDetails() {
//        FirebaseUtil.getBook(bookId, new FirebaseUtil.OnBookLoadedListener() {
//            @Override
//            public void onBookLoaded(Book loadedBook) {
//                book = loadedBook;
//                updateUI();
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(BookDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });
//    }
//
//    private void updateUI() {
//        Glide.with(this)
//                .load(book.getCoverImageUrl())
//                .placeholder(R.drawable.ic_book_placeholder)
//                .error(R.drawable.ic_book_placeholder)
//                .into(bookCover);
//
//        bookTitle.setText(book.getTitle());
//        bookAuthor.setText(book.getAuthor());
//        bookGenre.setText(book.getGenre());
//        bookDescription.setText(book.getDescription());
//        bookStatus.setText(book.getStatus());
//        bookRating.setText(String.format("%.1f (%d)", book.getRating(), book.getRatingCount()));
//
//        updateActionButton();
//        loadReviews();
//    }
//
//    private void updateActionButton() {
//        if (book.isAvailable()) {
//            actionButton.setText(R.string.borrow_book);
//            actionButton.setOnClickListener(v -> borrowBook());
//        } else if (book.getCurrentBorrowerId().equals(userId)) {
//            actionButton.setText(R.string.return_book);
//            actionButton.setOnClickListener(v -> returnBook());
//        } else {
//            actionButton.setText(R.string.reserve_book);
//            actionButton.setOnClickListener(v -> reserveBook());
//        }
//    }
//
//    private void borrowBook() {
//        FirebaseUtil.borrowBook(bookId, userId, new FirebaseUtil.OnBorrowingCompleteListener() {
//            @Override
//            public void onComplete() {
//                Toast.makeText(BookDetailsActivity.this, R.string.book_borrowed, Toast.LENGTH_SHORT).show();
//                loadBookDetails();
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(BookDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void returnBook() {
//        FirebaseUtil.returnBook(bookId, new FirebaseUtil.OnBorrowingCompleteListener() {
//            @Override
//            public void onComplete() {
//                Toast.makeText(BookDetailsActivity.this, R.string.book_returned, Toast.LENGTH_SHORT).show();
//                loadBookDetails();
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(BookDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void reserveBook() {
//        FirebaseUtil.reserveBook(bookId, userId, new FirebaseUtil.OnReservationCompleteListener() {
//            @Override
//            public void onComplete() {
//                Toast.makeText(BookDetailsActivity.this, R.string.book_reserved, Toast.LENGTH_SHORT).show();
//                loadBookDetails();
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(BookDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void loadReviews() {
//        FirebaseUtil.getBookReviews(bookId, new FirebaseUtil.OnReviewsLoadedListener() {
//            @Override
//            public void onReviewsLoaded(List<Review> reviews) {
//                reviewAdapter.setReviews(reviews);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(BookDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void showAddReviewDialog() {
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_review, null);
//        EditText reviewEditText = dialogView.findViewById(R.id.review_edit_text);
//        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
//
//        new AlertDialog.Builder(this)
//                .setTitle("Оставить отзыв")
//                .setView(dialogView)
//                .setPositiveButton("Отправить", (dialog, which) -> {
//                    String content = reviewEditText.getText().toString().trim();
//                    float rating = ratingBar.getRating();
//
//                    if (!content.isEmpty()) {
//                        Review review = new Review(userId, content, rating, System.currentTimeMillis());
//                        FirebaseUtil.addReview(bookId, review, new FirebaseUtil.OnReviewAddedListener() {
//                            @Override
//                            public void onSuccess() {
//                                Toast.makeText(BookDetailsActivity.this, "Отзыв добавлен", Toast.LENGTH_SHORT).show();
//                                loadReviews();
//                            }
//
//                            @Override
//                            public void onFailure(Exception e) {
//                                Toast.makeText(BookDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                        Toast.makeText(this, "Введите текст отзыва", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("Отмена", null)
//                .create()
//                .show();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}
package com.example.library.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.library.R;
import com.example.library.adapter.ReviewAdapter;
import com.example.library.model.Book;
import com.example.library.model.Review;
import com.example.library.util.FirebaseUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookDetailFragment extends Fragment {

    private static final String ARG_BOOK_ID = "book_id";

    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvGenre;
    private TextView tvYear;
    private TextView tvDescription;
    private TextView tvRating;
    private TextView tvStatus;
    private MaterialButton btnBorrow;
    private MaterialButton btnReserve;
    private ProgressBar progressBar;

    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private RatingBar ratingBar;
    private EditText etReview;
    private MaterialButton btnAddReview;

    private String bookId;
    private Book book;

    public static BookDetailFragment newInstance(String bookId) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookId = getArguments().getString(ARG_BOOK_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivCover = view.findViewById(R.id.ivCover);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvAuthor = view.findViewById(R.id.tvAuthor);
        tvGenre = view.findViewById(R.id.tvGenre);
        tvYear = view.findViewById(R.id.tvYear);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvRating = view.findViewById(R.id.tvRating);
        tvStatus = view.findViewById(R.id.tvStatus);
        btnBorrow = view.findViewById(R.id.btnBorrow);
        btnReserve = view.findViewById(R.id.btnReserve);
        progressBar = view.findViewById(R.id.progressBar);

        rvReviews = view.findViewById(R.id.rvReviews);
        ratingBar = view.findViewById(R.id.ratingBar);
        etReview = view.findViewById(R.id.etReview);
        btnAddReview = view.findViewById(R.id.btnAddReview);

        assert getArguments() != null;
        String bookId = getArguments().getString("book_id");

        setupRecyclerView();
        setupButtons();
        loadBook();
        loadReviews();
    }

    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvReviews.setAdapter(reviewAdapter);
    }

    private void setupButtons() {
        btnBorrow.setOnClickListener(v -> borrowBook());
        btnReserve.setOnClickListener(v -> reserveBook());
        btnAddReview.setOnClickListener(v -> addReview());
    }

    private void loadBook() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUtil.getBook(bookId,
                documentSnapshot -> {
                    book = documentSnapshot.toObject(Book.class);
                    if (book != null) {
                        updateUI();
                    }
                    progressBar.setVisibility(View.GONE);
                },
                e -> {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(requireView(), R.string.message_error, Snackbar.LENGTH_LONG).show();
                });
    }

    private void loadReviews() {
        FirebaseUtil.getReviewsByBook(bookId,
                queryDocumentSnapshots -> {
                    List<Review> reviews = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Review review = document.toObject(Review.class);
                        review.setId(document.getId());
                        reviews.add(review);
                    }
                    reviewAdapter.setReviews(reviews);
                },
                e -> Snackbar.make(requireView(), R.string.message_error, Snackbar.LENGTH_LONG).show());
    }

    private void updateUI() {
        Glide.with(this)
                .load(book.getCoverImageUrl())
                .placeholder(R.drawable.placeholder_book)
                .into(ivCover);

        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        tvGenre.setText(book.getGenre());
        tvYear.setText(String.valueOf(book.getYear()));
        tvDescription.setText(book.getDescription());
        tvRating.setText(String.format("%.1f", book.getRating()));
        tvStatus.setText(book.getStatus());

        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean isAvailable = "AVAILABLE".equals(book.getStatus());
        btnBorrow.setEnabled(isAvailable);
        btnReserve.setEnabled(!isAvailable);
    }

    private void borrowBook() {
        String userId = FirebaseUtil.getCurrentUserId();
        FirebaseUtil.borrowBook(bookId, userId,
                aVoid -> {
                    Snackbar.make(requireView(), R.string.message_book_borrowed, Snackbar.LENGTH_LONG).show();
                    loadBook();
                },
                e -> Snackbar.make(requireView(), R.string.message_error, Snackbar.LENGTH_LONG).show());
    }

    private void reserveBook() {
        String userId = FirebaseUtil.getCurrentUserId();
        FirebaseUtil.reserveBook(bookId, userId,
                aVoid -> {
                    Snackbar.make(requireView(), R.string.message_book_reserved, Snackbar.LENGTH_LONG).show();
                    loadBook();
                },
                e -> Snackbar.make(requireView(), R.string.message_error, Snackbar.LENGTH_LONG).show());
    }

    private void addReview() {
        String userId = FirebaseUtil.getCurrentUserId();
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String text = etReview.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (text.isEmpty()) {
            etReview.setError(getString(R.string.error_empty_review));
            return;
        }

        if (rating == 0) {
            Snackbar.make(requireView(), R.string.error_empty_rating, Snackbar.LENGTH_LONG).show();
            return;
        }

        Review review = new Review(userId, bookId, userName, text, rating);
        FirebaseUtil.createReview(review,
                documentReference -> {
                    review.setId(documentReference.getId());
                    List<Review> reviews = new ArrayList<>(reviewAdapter.getReviews());
                    reviews.add(0, review);
                    reviewAdapter.setReviews(reviews);
                    etReview.setText("");
                    ratingBar.setRating(0);
                    Snackbar.make(requireView(), R.string.message_review_added, Snackbar.LENGTH_LONG).show();
                },
                e -> Snackbar.make(requireView(), R.string.message_error, Snackbar.LENGTH_LONG).show());
    }
} 
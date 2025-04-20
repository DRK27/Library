package com.example.library.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.library.ui.activity.LoginActivity;
import com.example.library.R;
import com.example.library.data.model.User;
import com.example.library.data.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ImageView profileImageView;
    private TextView nameTextView, emailTextView, memberSinceTextView;
    private TextView booksBorrowedTextView, booksReservedTextView, quizzesTakenTextView, averageScoreTextView;
    private Button editProfileButton, logoutButton;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupButtons();
        loadUserProfile();
    }

    private void initViews(View view) {
        profileImageView = view.findViewById(R.id.profile_image);
        nameTextView = view.findViewById(R.id.name_text_view);
        emailTextView = view.findViewById(R.id.email_text_view);
        memberSinceTextView = view.findViewById(R.id.member_since_text_view);
        booksBorrowedTextView = view.findViewById(R.id.books_borrowed_text_view);
        booksReservedTextView = view.findViewById(R.id.books_reserved_text_view);
        quizzesTakenTextView = view.findViewById(R.id.quizzes_taken_text_view);
        averageScoreTextView = view.findViewById(R.id.average_score_text_view);
        logoutButton = view.findViewById(R.id.logout_button);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Please log in to view your profile", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        Log.d(TAG, "Loading profile for user: " + userId);

        FirebaseUtil.getUser(userId,
                documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) updateUI(user);
                    } else {
                        Log.w(TAG, "User document does not exist");
                        Toast.makeText(requireContext(), "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                },
                e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error loading user profile", e);
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void updateUI(User user) {
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        memberSinceTextView.setText(user.getMemberSince());
        booksBorrowedTextView.setText(String.valueOf(user.getBooksBorrowed()));
        booksReservedTextView.setText(String.valueOf(user.getBooksReserved()));
        quizzesTakenTextView.setText(String.valueOf(user.getQuizzesTaken()));
        averageScoreTextView.setText(String.format("%.1f", user.getAverageScore()));

        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(profileImageView);
        }
    }

    private void setupButtons() {

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
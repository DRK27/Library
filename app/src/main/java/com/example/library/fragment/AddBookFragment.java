package com.example.library.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.library.R;
import com.example.library.model.Book;
import com.example.library.util.FirebaseUtil;

public class AddBookFragment extends Fragment {
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText genreEditText;
    private EditText descriptionEditText;
    private Button addButton;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleEditText = view.findViewById(R.id.title_edit_text);
        authorEditText = view.findViewById(R.id.author_edit_text);
        genreEditText = view.findViewById(R.id.genre_edit_text);
        descriptionEditText = view.findViewById(R.id.description_edit_text);
        addButton = view.findViewById(R.id.add_button);
        progressBar = view.findViewById(R.id.progress_bar);

        addButton.setOnClickListener(v -> addBook());
    }

    private void addBook() {
        String title = titleEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        String genre = genreEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        addButton.setEnabled(false);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setDescription(description);
        book.setStatus(String.valueOf(Book.BookStatus.AVAILABLE));
        book.setRating(0.0);

        FirebaseUtil.addBook(book,
            documentReference -> {
                progressBar.setVisibility(View.GONE);
                addButton.setEnabled(true);
                Toast.makeText(requireContext(), "Book added successfully", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            },
            e -> {
                progressBar.setVisibility(View.GONE);
                addButton.setEnabled(true);
                Toast.makeText(requireContext(), "Error adding book: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );
    }
} 
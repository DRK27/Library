package com.example.library.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.R;
import com.example.library.adapter.BookAdapter;
import com.example.library.model.Book;
import com.example.library.util.FirebaseUtil;
import com.example.library.util.OpenLibraryUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private ProgressBar progressBar;
    private TextView emptyView;
    private boolean isSearching = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize views
        searchEditText = view.findViewById(R.id.search_edit_text);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);

        // Setup RecyclerView
        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookAdapter);

        // Setup search listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 3) {
                    searchBooks(s.toString());
                } else if (s.length() == 0) {
                    bookList.clear();
                    bookAdapter.notifyDataSetChanged();
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    private void searchBooks(String query) {
        if (isSearching) return;
        
        isSearching = true;
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        bookList.clear();
        bookAdapter.notifyDataSetChanged();

        // Search in Firebase
        FirebaseUtil.searchBooks(query,
            querySnapshot -> {
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Book book = document.toObject(Book.class);
                    book.setId(document.getId());
                    bookList.add(book);
                }
                
                bookAdapter.notifyDataSetChanged();
                isSearching = false;
                
                if (bookList.isEmpty()) {
                    searchOpenLibrary(query);
                } else {
                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                }
            },
            e -> {
                isSearching = false;
                progressBar.setVisibility(View.GONE);
                searchOpenLibrary(query);
            }
        );
    }
    
    private void searchOpenLibrary(String query) {
        OpenLibraryUtil.searchBooks(query, new OpenLibraryUtil.OnBooksLoadedListener() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                if (getActivity() == null) return;
                
                getActivity().runOnUiThread(() -> {
                    bookList.addAll(books);
                    bookAdapter.notifyDataSetChanged();
                    isSearching = false;
                    progressBar.setVisibility(View.GONE);
                    
                    if (bookList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                
                getActivity().runOnUiThread(() -> {
                    isSearching = false;
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    
                    if (bookList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
} 
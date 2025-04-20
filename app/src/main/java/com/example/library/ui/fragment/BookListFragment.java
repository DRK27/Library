package com.example.library.ui.fragment;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.R;
import com.example.library.ui.adapter.BookAdapter;
import com.example.library.data.model.Book;
import com.example.library.data.util.FirebaseUtil;
import com.example.library.data.util.OpenLibraryUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends Fragment implements BookAdapter.OnBookClickListener {
    private static final String TAG = "BookListFragment";

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private ProgressBar progressBar;
    private TextView emptyView;
    private FloatingActionButton addBookFab;
    private ChipGroup filterChipGroup;
    private Book.BookStatus currentFilter = Book.BookStatus.AVAILABLE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);
        addBookFab = view.findViewById(R.id.fab_add_book);
        filterChipGroup = view.findViewById(R.id.filter_chip_group);

        // Setup RecyclerView
        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookAdapter);

        // Setup FAB
        addBookFab.setOnClickListener(v -> {
            AddBookFragment addBookFragment = new AddBookFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, addBookFragment)
                    .addToBackStack(null)
                    .commit();
        });

        setupFilterChips();

        // Load books
        loadBooks();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadBooks();
    }

    private void setupFilterChips() {
        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            if (chip != null) {
                switch (chip.getText().toString()) {
                    case "Available":
                        currentFilter = Book.BookStatus.AVAILABLE;
                        break;
                    case "Borrowed":
                        currentFilter = Book.BookStatus.BORROWED;
                        break;
                    case "Reserved":
                        currentFilter = Book.BookStatus.RESERVED;
                        break;
                    default:
                        currentFilter = Book.BookStatus.AVAILABLE;
                }
                loadBooks();
            }
        });
    }

    private void loadBooks() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        Log.d(TAG, "Loading books with filter: " + currentFilter);

        FirebaseUtil.getBooksByStatus(currentFilter,
                querySnapshot -> {
                    Log.d(TAG, "Firebase query successful. Number of books: " + querySnapshot.size());
                    progressBar.setVisibility(View.GONE);
                    bookList.clear();

                    for (DocumentSnapshot document : querySnapshot) {
                        Book book = document.toObject(Book.class);
                        if (book != null) {
                            book.setId(document.getId());
                            bookList.add(book);
                            Log.d(TAG, "Added book: " + book.getTitle());
                        }
                    }

                    bookAdapter.notifyDataSetChanged();

                    if (bookList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setText("No books found with status: " + currentFilter);
                    }
                },
                e -> {
                    Log.e(TAG, "Error loading books from Firebase", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error loading books: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    emptyView.setVisibility(View.VISIBLE);
                }
        );
    }

    private void loadBooksFromOpenLibrary() {
        Log.d(TAG, "Loading books from Open Library...");

        OpenLibraryUtil.searchBooks("fiction", new OpenLibraryUtil.OnBooksLoadedListener() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    Log.d(TAG, "Open Library query successful. Number of books: " + books.size());
                    bookList.addAll(books);
                    bookAdapter.notifyDataSetChanged();

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
                    Log.e(TAG, "Error loading books from Open Library: " + error);
                    Toast.makeText(getContext(), "Error loading books: " + error, Toast.LENGTH_SHORT).show();

                    if (bookList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public void onBookClick(Book book) {
        BookDetailFragment fragment = BookDetailFragment.newInstance(book.getId());
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
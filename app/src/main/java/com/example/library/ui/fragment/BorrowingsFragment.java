package com.example.library.ui.fragment;

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
import com.example.library.ui.adapter.BorrowingAdapter;
import com.example.library.data.model.Borrowing;
import com.example.library.data.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BorrowingsFragment extends Fragment implements BorrowingAdapter.OnBorrowingActionListener {
    private static final String TAG = "BorrowingsFragment";
    
    private RecyclerView recyclerView;
    private BorrowingAdapter borrowingAdapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private List<Borrowing> borrowingList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrowings, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);
        
        borrowingList = new ArrayList<>();
        borrowingAdapter = new BorrowingAdapter(borrowingList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(borrowingAdapter);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadBorrowings();
    }

    private void loadBorrowings() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            progressBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("Please log in to view your borrowings");
            return;
        }
        
        String userId = auth.getCurrentUser().getUid();
        Log.d(TAG, "Loading borrowings for user: " + userId);
        
        FirebaseUtil.getBorrowingsByUser(userId, 
            querySnapshot -> {
                Log.d(TAG, "Firebase query successful. Number of borrowings: " + querySnapshot.size());
                progressBar.setVisibility(View.GONE);
                borrowingList.clear();
                
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Borrowing borrowing = document.toObject(Borrowing.class);
                    borrowing.setId(document.getId());
                    borrowingList.add(borrowing);
                    Log.d(TAG, "Added borrowing: " + borrowing.getBookId());
                }
                
                borrowingAdapter.notifyDataSetChanged();
                
                if (borrowingList.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                }
            },
            e -> {
                Log.e(TAG, "Error loading borrowings from Firebase", e);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading borrowings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                emptyView.setVisibility(View.VISIBLE);
            }
        );
    }

    @Override
    public void onReturnBook(Borrowing borrowing) {
        // TODO: Implement return book functionality
    }

    @Override
    public void onTakeQuiz(Borrowing borrowing) {
        // TODO: Implement take quiz functionality
    }
} 
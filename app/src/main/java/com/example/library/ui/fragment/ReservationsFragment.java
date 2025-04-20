package com.example.library.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.R;
import com.example.library.ui.adapter.ReservationAdapter;
import com.example.library.data.model.Reservation;
import com.example.library.data.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReservationsFragment extends Fragment implements ReservationAdapter.OnReservationActionListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ReservationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        setupRecyclerView();
        loadReservations();
    }

    private void setupRecyclerView() {
        adapter = new ReservationAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadReservations() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        FirebaseUtil.getReservationsByUser(userId,
                queryDocumentSnapshots -> {
                    List<Reservation> reservations = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Reservation reservation = document.toObject(Reservation.class);
                        reservation.setId(document.getId());
                        reservations.add(reservation);
                    }

                    progressBar.setVisibility(View.GONE);
                    if (reservations.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setReservations(reservations);
                    }
                },
                e -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText(R.string.error_loading_reservations);
                });
    }

    @Override
    public void onCancelReservation(Reservation reservation) {
        reservation.setStatus("CANCELLED");
        FirebaseUtil.updateReservation(reservation,
                aVoid -> loadReservations(),
                e -> {
                    // Show error message
                });
    }
} 
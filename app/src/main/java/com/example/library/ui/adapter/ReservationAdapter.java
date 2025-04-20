package com.example.library.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.R;
import com.example.library.data.model.Reservation;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> reservations = new ArrayList<>();
    private final OnReservationActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public interface OnReservationActionListener {
        void onCancelReservation(Reservation reservation);
    }

    public ReservationAdapter(OnReservationActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        holder.bind(reservations.get(position));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
        notifyDataSetChanged();
    }

    class ReservationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBookTitle;
        private final TextView tvStatus;
        private final TextView tvReservationDate;
        private final TextView tvExpiryDate;
        private final MaterialButton btnCancel;

        ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvReservationDate = itemView.findViewById(R.id.tvReservationDate);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }

        void bind(Reservation reservation) {
            tvBookTitle.setText(reservation.getBookTitle());
            tvStatus.setText(reservation.getStatus());
            tvReservationDate.setText(String.format("Reserved on: %s", 
                    dateFormat.format(reservation.getReservationDate())));
            tvExpiryDate.setText(String.format("Expires on: %s", 
                    dateFormat.format(reservation.getExpiryDate())));

            btnCancel.setOnClickListener(v -> listener.onCancelReservation(reservation));
        }
    }
} 
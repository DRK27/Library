package com.example.library.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.library.R;
import com.example.library.model.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> books;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BookAdapter(List<Book> books) {
        this.books = books;
    }

    public void setOnBookClickListener(OnBookClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bind(book, listener);
//        holder.itemView.setOnClickListener(v -> {
//            listener.onBookClick(book);
//        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        private ImageView coverImageView;
        private TextView titleTextView;
        private TextView authorTextView;
        private TextView genreTextView;
        private TextView statusTextView;
        private TextView ratingTextView;
        private TextView sourceTextView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.cover_image_view);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            authorTextView = itemView.findViewById(R.id.author_text_view);
            genreTextView = itemView.findViewById(R.id.genre_text_view);
            statusTextView = itemView.findViewById(R.id.status_text_view);
            ratingTextView = itemView.findViewById(R.id.rating_text_view);
            sourceTextView = itemView.findViewById(R.id.source_text_view);
        }

        public void bind(Book book, OnBookClickListener listener) {
            titleTextView.setText(book.getTitle());
            authorTextView.setText(book.getAuthor());
            genreTextView.setText(book.getGenre());
            
            String statusText;
            int statusColor;
            switch (book.getStatus()) {
                case "AVAILABLE":
                    statusText = "Available";
                    statusColor = R.color.colorSuccess;
                    break;
                case "BORROWED":
                    statusText = "Borrowed";
                    statusColor = R.color.colorWarning;
                    break;
                case "RESERVED":
                    statusText = "Reserved";
                    statusColor = R.color.colorError;
                    break;
                default:
                    statusText = "Unknown";
                    statusColor = R.color.colorTextSecondary;
            }
            statusTextView.setText(statusText);
            statusTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), statusColor));
            
            ratingTextView.setText(String.format("%.1f", book.getRating()));
            
            if (book.isFromOpenLibrary()) {
                sourceTextView.setText("Open Library");
                sourceTextView.setVisibility(View.VISIBLE);
            } else {
                sourceTextView.setVisibility(View.GONE);
            }
            
            if (book.getCoverImageUrl() != null && !book.getCoverImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(book.getCoverImageUrl())
                        .placeholder(R.drawable.ic_book_placeholder)
                        .error(R.drawable.ic_book_placeholder)
                        .into(coverImageView);
            } else {
                coverImageView.setImageResource(R.drawable.ic_book_placeholder);
            }
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookClick(book);
                }
            });
        }
    }
} 
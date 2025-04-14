package com.example.library.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.R;
import com.example.library.fragment.BorrowingsFragment;
import com.example.library.model.Borrowing;
import com.example.library.util.DateUtil;

import java.util.List;

public class BorrowingAdapter extends RecyclerView.Adapter<BorrowingAdapter.BorrowingViewHolder> {
    private List<Borrowing> borrowings;
    private OnBorrowingActionListener listener;

    public interface OnBorrowingActionListener {
        void onReturnBook(Borrowing borrowing);
        void onTakeQuiz(Borrowing borrowing);
    }

    public BorrowingAdapter(List<Borrowing> borrowings, BorrowingsFragment borrowingsFragment) {
        this.borrowings = borrowings;
    }

    public void setOnBorrowingActionListener(OnBorrowingActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BorrowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_borrowing, parent, false);
        return new BorrowingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowingViewHolder holder, int position) {
        Borrowing borrowing = borrowings.get(position);
        holder.bind(borrowing);
    }

    @Override
    public int getItemCount() {
        return borrowings.size();
    }

    class BorrowingViewHolder extends RecyclerView.ViewHolder {
        private TextView bookTitleTextView;
        private TextView borrowDateTextView;
        private TextView dueDateTextView;
        private TextView statusTextView;
        private Button returnButton;
        private Button quizButton;

        public BorrowingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitleTextView = itemView.findViewById(R.id.book_title_text);
            borrowDateTextView = itemView.findViewById(R.id.borrow_date_text);
            dueDateTextView = itemView.findViewById(R.id.due_date_text);
            statusTextView = itemView.findViewById(R.id.status_text);
            returnButton = itemView.findViewById(R.id.return_button);
            quizButton = itemView.findViewById(R.id.quiz_button);

            returnButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onReturnBook(borrowings.get(position));
                }
            });

            quizButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTakeQuiz(borrowings.get(position));
                }
            });
        }

        public void bind(Borrowing borrowing) {
            bookTitleTextView.setText("Book Title"); // TODO: Load book title
            borrowDateTextView.setText("Borrowed: " + DateUtil.formatDate(borrowing.getBorrowDate()));
            dueDateTextView.setText("Due: " + DateUtil.formatDate(borrowing.getDueDate()));
            
            String status;
            int statusColor;
            if (borrowing.isOverdue()) {
                status = "OVERDUE";
                statusColor = R.color.colorError;
            } else if (borrowing.isReturned()) {
                status = "RETURNED";
                statusColor = R.color.colorSuccess;
            } else {
                status = "ACTIVE";
                statusColor = R.color.colorPrimary;
            }
            
            statusTextView.setText(status);
            statusTextView.setTextColor(itemView.getContext().getColor(statusColor));

            returnButton.setVisibility(borrowing.isActive() ? View.VISIBLE : View.GONE);
            quizButton.setVisibility(!borrowing.isQuizCompleted() ? View.VISIBLE : View.GONE);
        }
    }
} 
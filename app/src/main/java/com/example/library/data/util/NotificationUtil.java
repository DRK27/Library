package com.example.library.data.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.library.ui.activity.MainActivity;
import com.example.library.R;
import com.example.library.data.model.Book;
import com.example.library.data.model.Borrowing;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotificationUtil {
    private static final String CHANNEL_ID_BOOK_DUE = "book_due_channel";
    private static final String CHANNEL_ID_BOOK_OVERDUE = "book_overdue_channel";
    private static final String CHANNEL_ID_BOOK_AVAILABLE = "book_available_channel";
    private static final String CHANNEL_ID_RESERVATION = "reservation_channel";
    
    private static final int NOTIFICATION_ID_BOOK_DUE = 1001;
    private static final int NOTIFICATION_ID_BOOK_OVERDUE = 1002;
    private static final int NOTIFICATION_ID_BOOK_AVAILABLE = 1003;
    private static final int NOTIFICATION_ID_RESERVATION = 1004;
    

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            
            // Book due channel
            NotificationChannel bookDueChannel = new NotificationChannel(
                    CHANNEL_ID_BOOK_DUE,
                    "Book Due Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            bookDueChannel.setDescription("Notifications for books due soon");
            
            // Book overdue channel
            NotificationChannel bookOverdueChannel = new NotificationChannel(
                    CHANNEL_ID_BOOK_OVERDUE,
                    "Book Overdue Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            bookOverdueChannel.setDescription("Notifications for overdue books");
            
            // Book available channel
            NotificationChannel bookAvailableChannel = new NotificationChannel(
                    CHANNEL_ID_BOOK_AVAILABLE,
                    "Book Available Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            bookAvailableChannel.setDescription("Notifications for available books");
            
            // Reservation channel
            NotificationChannel reservationChannel = new NotificationChannel(
                    CHANNEL_ID_RESERVATION,
                    "Reservation Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            reservationChannel.setDescription("Notifications for reservations");
            
            // Create the channels
            notificationManager.createNotificationChannels(java.util.Arrays.asList(
                    bookDueChannel,
                    bookOverdueChannel,
                    bookAvailableChannel,
                    reservationChannel
            ));
        }
    }
    

    public static void showBookDueNotification(Context context, Book book, Borrowing borrowing) {
        // Create an intent for the notification
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("bookId", book.getId());
        
        // Create a pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Format the due date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String dueDate = dateFormat.format(borrowing.getDueDate());
        
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_BOOK_DUE)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Book Due Soon: " + book.getTitle())
                .setContentText("Your book is due on " + dueDate)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID_BOOK_DUE, builder.build());
    }

    public static void showBookOverdueNotification(Context context, Book book, Borrowing borrowing) {
        // Create an intent for the notification
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("bookId", book.getId());
        
        // Create a pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Format the due date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String dueDate = dateFormat.format(borrowing.getDueDate());
        
        // Calculate days overdue
        long daysOverdue = borrowing.getDaysOverdue();
        
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_BOOK_OVERDUE)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Book Overdue: " + book.getTitle())
                .setContentText("Your book is " + daysOverdue + " days overdue (due on " + dueDate + ")")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID_BOOK_OVERDUE, builder.build());
    }
    

    public static void showBookAvailableNotification(Context context, Book book) {
        // Create an intent for the notification
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("bookId", book.getId());
        
        // Create a pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_BOOK_AVAILABLE)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Book Available: " + book.getTitle())
                .setContentText("The book you reserved is now available")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID_BOOK_AVAILABLE, builder.build());
    }
    

    public static void showReservationNotification(Context context, Book book) {
        // Create an intent for the notification
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("bookId", book.getId());
        
        // Create a pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_RESERVATION)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Book Reserved: " + book.getTitle())
                .setContentText("You have successfully reserved this book")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID_RESERVATION, builder.build());
    }
} 
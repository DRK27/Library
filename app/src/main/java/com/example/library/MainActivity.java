package com.example.library;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.library.fragment.BookListFragment;
import com.example.library.fragment.BorrowingsFragment;
import com.example.library.fragment.ProfileFragment;
import com.example.library.fragment.SearchFragment;
import com.example.library.util.NotificationUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FirebaseAuth firebaseAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        
        NotificationUtil.createNotificationChannels(this);
        
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        
        fragmentManager = getSupportFragmentManager();
        
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            showLoginScreen();
        } else {
            showFragment(new BookListFragment());
        }
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_books) {
            fragment = new BookListFragment();
        } else if (itemId == R.id.navigation_search) {
            fragment = new SearchFragment();
        } else if (itemId == R.id.navigation_borrowings) {
            fragment = new BorrowingsFragment();
        } else if (itemId == R.id.navigation_profile) {
            fragment = new ProfileFragment();
        }
        
        if (fragment != null) {
            showFragment(fragment);
            return true;
        }
        
        return false;
    }
    
    private void showFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
    
    private void showLoginScreen() {
        // TODO: Implement login screen
    }
}
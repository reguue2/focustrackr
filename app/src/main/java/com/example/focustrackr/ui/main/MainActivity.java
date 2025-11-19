package com.example.focustrackr.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.focustrackr.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmail = getIntent().getStringExtra("USER_EMAIL");

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            if (item.getItemId() == R.id.nav_sessions) {
                selected = new SessionListFragment();
            } else if (item.getItemId() == R.id.nav_stats) {
                selected = new StatsFragment();
            }
            if (selected != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, selected)
                        .commit();
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_sessions);
        }
    }

    public String getUserEmail() {
        return userEmail;
    }
}

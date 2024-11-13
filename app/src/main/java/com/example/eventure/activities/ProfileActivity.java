package com.example.eventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventure.R;
import com.example.eventure.fragments.EventsFragment;
import com.example.eventure.fragments.PasFragment;
import com.example.eventure.fragments.ProfileStartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private NavigationView navigationView;
//    private ActionBar actionBar;
//    private ActionBarDrawerToggle actionBarDrawerToggle;

//    private Set<Integer> topLevelDestinations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("EVENTURE");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notifications, R.id.nav_messages, R.id.nav_favorite_products,
                R.id.nav_favorite_services, R.id.nav_favorite_events, R.id.nav_my_calendar
        ).setOpenableLayout(drawer).build();

        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.events_menu) {
                selectedFragment = new EventsFragment();
            } else if (itemId == R.id.pas_menu) {
                selectedFragment = new PasFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });
    }


//    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
//        // By using switch we can easily get
//        // the selected fragment
//        // by using there id.
//        Fragment selectedFragment = null;
//        int itemId = item.getItemId();
//        if (itemId == R.id.events_menu) {
//            selectedFragment = new EventsFragment();
//        } else if (itemId == R.id.pas_menu) {
//            selectedFragment = new PasFragment();
//        }
//        if (selectedFragment != null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
//        }
//        return true;
//    };

}
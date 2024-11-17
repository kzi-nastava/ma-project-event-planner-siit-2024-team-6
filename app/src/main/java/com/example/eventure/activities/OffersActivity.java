package com.example.eventure.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventure.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class OffersActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private NavigationView navigationView;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextStyle);
        toolbar.setContentInsetStartWithNavigation(70);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("EVENTURE");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        drawer = findViewById(R.id.drawer_offers_layout);
        navigationView = findViewById(R.id.sidebar_view);

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main_home);

        BottomNavigationView bottomNav = findViewById(R.id.offers_bottom_navigation);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_humburger);
            actionBar.setHomeButtonEnabled(true);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notifications, R.id.nav_messages, R.id.nav_favorite_products,
                R.id.nav_favorite_services, R.id.nav_favorite_events, R.id.nav_my_calendar,
                R.id.pas_menu, R.id.events_menu
        ).setOpenableLayout(drawer).build();


        NavigationUI.setupWithNavController(bottomNav, navController);

        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        // Find profile icon in toolbar
        @SuppressLint("ResourceType") View profileIcon = toolbar.findViewById(R.id.nav_profile);
        profileIcon.setOnClickListener(v -> {
            // Create an Intent to start ProfileActivity
            Intent intent = new Intent(OffersActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Action listener for creating new services/products
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // Navigate to TargetActivity
            //Intent intent = new Intent(this, TargetActivity.class);
            //startActivity(intent);
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("EVENTURE");
            }
        });


    }

}

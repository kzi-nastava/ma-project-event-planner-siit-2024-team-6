package com.example.eventure.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventure.R;
import com.example.eventure.dialogs.CreateServiceDialog;
import com.example.eventure.fragments.ProviderServicesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class ProviderOffersActivity extends AppCompatActivity {
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_offers_layout);
        navigationView = findViewById(R.id.sidebar_view);
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main_home);

        NavigationView navigationView = findViewById(R.id.sidebar_view);

        // Set listener for navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_messages) {
                startActivity(new Intent(this, ChatActivity.class));
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, HomeActivity.class).putExtra("FRAGMENT_NAME", "NOTIFICATIONS"));
            } else if (id == R.id.nav_favorite_events) {
                startActivity(new Intent(this, HomeActivity.class).putExtra("FRAGMENT_NAME", "FAVOURITE_EVENTS"));
            } else if (id == R.id.nav_favorite_services) {
                startActivity(new Intent(this, HomeActivity.class).putExtra("FRAGMENT_NAME", "FAVOURITE_SERVICES"));
            } else if (id == R.id.nav_favorite_products) {
                startActivity(new Intent(this, HomeActivity.class).putExtra("FRAGMENT_NAME", "FAVOURITE_PRODUCTS"));
            } else if (id == R.id.nav_my_calendar) {
                startActivity(new Intent(this, HomeActivity.class).putExtra("FRAGMENT_NAME", "CALENDAR"));
            }  else if (id == R.id.nav_admin_categories) {
                startActivity(new Intent(this, AdminCategoriesActivity.class));
            } else if (id == R.id.nav_admin_manage_comments) {
                startActivity(new Intent(this,AdminCommentsActivity.class));
            } else if (id == R.id.nav_admin_manage_reports){
                startActivity(new Intent(this,AdminReportsActivity.class));
            } else if (id == R.id.nav_price_list) {
                startActivity(new Intent(this,ProviderPriceListActivity.class));
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        // Find profile icon in toolbar
        @SuppressLint("ResourceType") View profileIcon = toolbar.findViewById(R.id.nav_profile);
        profileIcon.setOnClickListener(v -> {
            // Create an Intent to start ProfileActivity
            Intent intent = new Intent(ProviderOffersActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        TextView tvTitle = toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderOffersActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });


        // Action listener for creating new services/products
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            int currentFragmentId = navController.getCurrentDestination().getId();
            if (currentFragmentId == R.id.services_menu) {
                CreateServiceDialog dialog = new CreateServiceDialog();
                dialog.setOnOfferCreatedListener(() -> {
                    // Refresh the current fragment
                    navController.navigate(R.id.services_menu);
                });
                dialog.show(getSupportFragmentManager(), "CreateServiceDialog");
            }
        });
        // Enable search by name
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("OffersActivity", "onQueryTextSubmit: " + query);
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("OffersActivity", "onQueryTextChange: " + newText);
                performSearch(newText);
                return true;
            }
        });

        Log.d("OffersActivity", "SearchView listener attached");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    // Method to perform search
    private void performSearch(String query) {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_nav_content_main_home)
                .getChildFragmentManager()
                .getPrimaryNavigationFragment();

        if (currentFragment instanceof ProviderServicesFragment) {
            ((ProviderServicesFragment) currentFragment).searchServices(query);
        } else {
            Log.d("OffersActivity", "Current fragment is not ProviderServicesFragment");
        }
    }


}

package com.example.eventure.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.CreateServiceDialog;
import com.example.eventure.fragments.ProviderServicesFragment;
import com.example.eventure.utils.MenuUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class AdminCategoriesActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private NavController navController;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextStyle);
        toolbar.setContentInsetStartWithNavigation(70);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_categories_layout);
        navigationView = findViewById(R.id.sidebar_view);
        String role = ClientUtils.getAuthService().getRole();
        MenuUtils.filterMenuByRole(navigationView, role);
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_categories);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            String fragment = null;
            if (id == R.id.nav_messages) {
                Intent intent = new Intent(AdminCategoriesActivity.this, ChatActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_notifications) {
                fragment = "NOTIFICATIONS";
            } else if (id == R.id.nav_favorite_events) {
                fragment = "FAVOURITE_EVENTS";
            } else if (id == R.id.nav_favorite_services) {
                fragment = "FAVOURITE_SERVICES";
            } else if (id == R.id.nav_favorite_products) {
                fragment = "FAVOURITE_PRODUCTS";
            } else if (id == R.id.nav_my_calendar) {
                fragment = "CALENDAR";
            }  else if (id == R.id.nav_admin_manage_comments) {
                startActivity(new Intent(this,AdminCommentsActivity.class));
            } else if (id == R.id.nav_admin_manage_reports) {
                startActivity(new Intent(this, AdminReportsActivity.class));
            } else if (id == R.id.nav_price_list){
                startActivity(new Intent(this, AdminReportsActivity.class));
            } else if(id == R.id.nav_event_types){
                startActivity(new Intent(this,AdminEventTypesActivity.class));
            }
            if (fragment != null) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("FRAGMENT_NAME", fragment);
                startActivity(intent);
                finish();
                return true;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_categories);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_categories);
        NavigationUI.setupWithNavController(bottomNav, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.d("NavigationDebug", "Navigated to: " + destination.getLabel());
        });


        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();




        // Find profile icon in toolbar
        @SuppressLint("ResourceType") View profileIcon = toolbar.findViewById(R.id.nav_profile);
        profileIcon.setOnClickListener(v -> {
            // Create an Intent to start ProfileActivity
            Intent intent = new Intent(AdminCategoriesActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        TextView tvTitle = toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setOnClickListener(v -> {
            Intent intent = new Intent(AdminCategoriesActivity.this, HomeActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

}

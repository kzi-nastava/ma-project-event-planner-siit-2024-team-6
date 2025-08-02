package com.example.eventure.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventure.R;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.CreateEventTypeDialog;
import com.example.eventure.utils.MenuUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class AdminEventTypesActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_types);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextStyle);
        toolbar.setContentInsetStartWithNavigation(70);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_event_types_layout);
        NavigationView navigationView = findViewById(R.id.sidebar_view);
        String role = ClientUtils.getAuthService().getRole();
        MenuUtils.filterMenuByRole(navigationView, role);

        navController = Navigation.findNavController(this, R.id.fragment_nav_event_types);

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
            } else if (id == R.id.nav_messages) {
                navController.navigate(R.id.nav_messages);
                Intent intent = new Intent(AdminEventTypesActivity.this, ChatActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_admin_categories) {
                Intent intent = new Intent(AdminEventTypesActivity.this, AdminCategoriesActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_admin_manage_comments) {
                Intent intent = new Intent(AdminEventTypesActivity.this, AdminCommentsActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_admin_manage_reports) {
                Intent intent = new Intent(AdminEventTypesActivity.this, AdminReportsActivity.class);
                startActivity(intent);
                return true;
            } else if(id == R.id.nav_price_list){
                Intent intent = new Intent(AdminEventTypesActivity.this, ProviderPriceListActivity.class);
                startActivity(intent);
                return true;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        BottomNavigationView bottomNav = findViewById(R.id.event_types_bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        @SuppressLint("ResourceType") View profileIcon = toolbar.findViewById(R.id.nav_profile);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(AdminEventTypesActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        TextView tvTitle = toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setOnClickListener(v -> {
            Intent intent = new Intent(AdminEventTypesActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        FloatingActionButton fab = findViewById(R.id.fab_event_type);
        fab.setOnClickListener(view -> {
            int currentFragmentId = navController.getCurrentDestination().getId();
            if (currentFragmentId == R.id.AdminEventTypesFragment) {
                CreateEventTypeDialog dialog = new CreateEventTypeDialog();
                dialog.setOnEventTypeCreatedListener(() -> {
                    navController.navigate(R.id.AdminEventTypesFragment);
                });
                dialog.show(getSupportFragmentManager(), "CreateEventTypeDialog");
            }
        });

        SearchView searchView = findViewById(R.id.search_view_event_types);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // performSearch(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}

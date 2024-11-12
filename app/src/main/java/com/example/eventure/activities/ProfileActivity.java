package com.example.eventure.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import com.example.eventure.fragments.EventsFragment;
import com.example.eventure.fragments.PasFragment;
import com.example.eventure.fragments.ProfileStartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private Set<Integer> topLevelDestinations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextStyle);
        toolbar.setContentInsetStartWithNavigation(70);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("EVENTURE");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        // Настройка DrawerLayout и NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Настройка NavController для бокового меню
        navController = Navigation.findNavController(this, R.id.fragment_container);

        // Настройка AppBarConfiguration и добавление верхнеуровневых фрагментов
        topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.nav_notifications);
        topLevelDestinations.add(R.id.nav_messages);
        topLevelDestinations.add(R.id.nav_favorite_events);
        topLevelDestinations.add(R.id.nav_ft_products);
        topLevelDestinations.add(R.id.nav_ft_services);
        topLevelDestinations.add(R.id.nav_my_calendar);

        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                .setOpenableLayout(drawerLayout)
                .build();

        // Настройка ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Настройка NavigationView с NavController
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Добавление OnDestinationChangedListener для обработки изменения фрагмента
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            Log.i("ProfileActivity", "Destination Changed: " + destination.getLabel());

            boolean isTopLevelDestination = topLevelDestinations.contains(id);

            if (!isTopLevelDestination) {
                if (id == R.id.nav_notifications) {
                    Toast.makeText(ProfileActivity.this, "Notifications", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_messages) {
                    Toast.makeText(ProfileActivity.this, "Messages", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_favorite_events) {
                    Toast.makeText(ProfileActivity.this, "Favorite Events", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_ft_products) {
                    Toast.makeText(ProfileActivity.this, "F-T Products", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_ft_services) {
                    Toast.makeText(ProfileActivity.this, "F-T Services", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_my_calendar) {
                    Toast.makeText(ProfileActivity.this, "My Calendar", Toast.LENGTH_SHORT).show();
                }
                // Закрываем боковое меню при переходе к нефрагменту верхнего уровня
                drawerLayout.closeDrawers();
            }
        });

        // Обработка выбора элементов в NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItemSelected(item);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Настройка BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Загрузка стартового фрагмента
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileStartFragment())
                    .commit();
        }
    }

    // Обработка выбора элементов в боковом меню
    private void handleNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Fragment selectedFragment = null;

        switch (itemId) {
            case R.id.nav_notifications:
                // Добавьте логику или фрагмент для "Notifications"
                break;
            case R.id.nav_messages:
                // Добавьте логику или фрагмент для "Messages"
                break;
            case R.id.nav_favorite_events:
                // Добавьте логику или фрагмент для "Favorite Events"
                break;
            case R.id.nav_favorite_products:
                // Добавьте логику или фрагмент для "F-T Products"
                break;
            case R.id.nav_favorite_services:
                // Добавьте логику или фрагмент для "F-T Services"
                break;
            case R.id.nav_my_calendar:
                // Добавьте логику или фрагмент для "My Calendar"
                break;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }
    }

    // Обработка выбора элементов в нижнем меню навигации
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.events_menu) {
            selectedFragment = new EventsFragment();
        } else if (itemId == R.id.pas_menu) {
            selectedFragment = new PasFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }
        return true;
    };

    // Метод для поддержки кнопки назад с DrawerLayout
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Метод для поддержки кнопки вверх с DrawerLayout
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

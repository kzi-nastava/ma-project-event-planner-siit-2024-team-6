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
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private Set<Integer> topLevelDestinations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextStyle);
        toolbar.setContentInsetStartWithNavigation(70);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("EVENTURE");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

//        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
//        NavigationUI.setupWithNavController(bottomNav, navController);
//
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new ProfileStartFragment())
//                    .commit();
//        }
//
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            // postavlja prikazivanje "strelice prema nazad" (back arrow)
            // kao indikatora navigacije na lijevoj strani Toolbar-a.
            actionBar.setDisplayHomeAsUpEnabled(false);
            // postavlja ikonu koja se prikazuje umjesto strelice prema nazad.
            // U ovom slučaju, postavljena je ikona hamburger iz drawable resursa (ic_hamburger).
            actionBar.setHomeAsUpIndicator(R.drawable.ic_humburger);
            //ovo omogućuje da se klikom na 'home' na Toolbar-u
            // aktivira povratak na prethodnu aktivnost.
            actionBar.setHomeButtonEnabled(false);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            int id = navDestination.getId();
            boolean isTopLevelDestination = topLevelDestinations.contains(id);
            /* Logic to determine if the destination is top level */;
            if (!isTopLevelDestination) {
                if (id == R.id.nav_notifications) {
                    Toast.makeText(ProfileActivity.this, "notifications", Toast.LENGTH_SHORT).show();
                    /* Do something when this item is selected,
                     * such as navigating to a specific fragment
                     * For example:
                     * navController.navigate(R.id.nav_products);
                     * Replace with your destination fragment ID
                     */
                } else if (id == R.id.nav_messages) {
                    Toast.makeText(ProfileActivity.this, "messages", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_favorite_events) {
                    Toast.makeText(ProfileActivity.this, "favorite events", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_favorite_products) {
                    Toast.makeText(ProfileActivity.this, "favorite products", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_favorite_services) {
                    Toast.makeText(ProfileActivity.this, "favorite services", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_my_calendar) {
                    Toast.makeText(ProfileActivity.this, "my calendar", Toast.LENGTH_SHORT).show();
                }
                // Close the drawer if the destination is not a top-level destination
                drawer.closeDrawers();
            }
//            } else {
//                if (id == R.id.nav_settings) {
//                    Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
//                } else if (id == R.id.nav_language) {
//                    Toast.makeText(HomeActivity.this, "Language", Toast.LENGTH_SHORT).show();
//                }
//            }
        });

        appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.nav_notifications, R.id.nav_messages, R.id.nav_favorite_products, R.id.nav_favorite_services, R.id.nav_favorite_events, R.id.nav_my_calendar)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupWithNavController(navigationView, navController);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_profile)
                .build();

        // Связь BottomNavigationView с NavController
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Связываем Toolbar с NavController для отображения кнопки назад
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        // By using switch we can easily get
        // the selected fragment
        // by using there id.
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
    };

}
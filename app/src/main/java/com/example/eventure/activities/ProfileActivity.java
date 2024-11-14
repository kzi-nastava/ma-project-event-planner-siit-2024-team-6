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

//    private Set<Integer> topLevelDestinations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("EVENTURE");

        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);

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
        // dodajemo navigation drawer-u listener za događaje koji se dese.
        // actionBarDrawerToggle prati promene stanja drawera i reaguje na njih.
        drawer.addDrawerListener(actionBarDrawerToggle);
        // syncState() se koristi kako bi se uskladile ikone (npr. "hamburger" ikona)
        // i stanja između ActionBar-a (ili Toolbar-a) i drawer-a. Ova metoda osigurava
        // da se ikona na ActionBar-u (ili Toolbar-u) pravilno menja u zavisnosti
        // od stanja drawer-a (otvoreno ili zatvoreno).
        actionBarDrawerToggle.syncState();

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notifications, R.id.nav_messages, R.id.nav_favorite_products,
                R.id.nav_favorite_services, R.id.nav_favorite_events, R.id.nav_my_calendar
        ).setOpenableLayout(drawer).build();

        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


    }


    // In ProfileActivity.java
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        // Get the selected menu item ID
        int itemId = item.getItemId();

        if (itemId == R.id.events_menu) {
            // Go to HomeActivity and show EventsFragment
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.putExtra("fragment_key", "events");  // Pass the fragment type
            startActivity(intent);
            finish();  // Finish ProfileActivity so the user can't go back
            return true;
        } else if (itemId == R.id.pas_menu) {
            // Go to HomeActivity and show PasFragment
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.putExtra("fragment_key", "pas");  // Pass the fragment type
            startActivity(intent);
            finish();  // Finish ProfileActivity so the user can't go back
            return true;
        }
        return false;
    };


}
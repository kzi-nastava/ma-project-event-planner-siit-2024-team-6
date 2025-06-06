package com.example.eventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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

//import com.denzcoskun.imageslider.ImageSlider;
//import com.denzcoskun.imageslider.models.SlideModel;
import com.example.eventure.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

//onCreate, onStart, onRestart, onResume, onPause, onStop, onDestroy.
public class HomeActivity extends AppCompatActivity {
    /*
     * Unutar onCreate metode, postavljamo izgled nase aktivnosti koristeci setContentView
     * U ovoj metodi mozemo dobaviti sve view-e (widget-e, komponente interface-a).
     * Moramo voditi racuna, ovde se ne sme nalaziti kod koji ce blokirati prelazak aktivnosti
     * u naredne metode! To znaci da izvrsavanje dugackih operacija treba izbegavati ovde.
     * */

    private DrawerLayout drawer;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private NavigationView navigationView;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextStyle);
        toolbar.setContentInsetStartWithNavigation(70);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_home_layout);
        navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main_home);


        String fragmentName = getIntent().getStringExtra("FRAGMENT_NAME");
        if (fragmentName != null) {
            switch (fragmentName) {
                case "FAVOURITE_EVENTS":
                    navController.navigate(R.id.nav_favorite_events); // Navigate to My Offers fragment
                    break;
                case "NOTIFICATIONS":
                    navController.navigate(R.id.nav_notifications); // Navigate to Notifications fragment
                    break;
                case "MESSAGES":
                    navController.navigate(R.id.nav_messages); // Navigate to Messages fragment
                    break;
                case "FAVOURITE_SERVICES":
                    navController.navigate(R.id.nav_favorite_services); // Navigate to My Offers fragment
                    break;
                case "FAVOURITE_PRODUCTS":
                    navController.navigate(R.id.nav_favorite_products); // Navigate to My Offers fragment
                    break;
                case "CALENDAR":
                    navController.navigate(R.id.nav_my_calendar); // Navigate to My Offers fragment
                    break;
                case "CATEGORIES":
                    navController.navigate(R.id.nav_admin_categories); // Navigate to My Offers fragment
                    break;
                default:
                    // Optionally handle unknown fragment names
                    break;
            }
        }

        // Setup navigation item selection manually
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_my_offers) {
                Intent intent = new Intent(HomeActivity.this, ProviderOffersActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_notifications) {
                navController.navigate(R.id.nav_notifications);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_messages) {
                navController.navigate(R.id.nav_messages);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_admin_categories) {
                navController.navigate(R.id.nav_admin_categories);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

            drawer.closeDrawer(GravityCompat.START);
            return false;
        });

        // BottomNavigationView setup
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
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


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ShopApp", "HomeActivity onStart()");
    }


    @Override
    protected void onResume(){
        super.onResume();
        Log.d("ShopApp", "HomeActivity onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ShopApp", "HomeActivity onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ShopApp", "HomeActivity onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ShopApp", "HomeActivity onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("ShopApp", "HomeActivity onRestart()");
    }
}
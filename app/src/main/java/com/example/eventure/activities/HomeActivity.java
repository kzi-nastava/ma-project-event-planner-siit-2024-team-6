package com.example.eventure.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

//import com.denzcoskun.imageslider.ImageSlider;
//import com.denzcoskun.imageslider.models.SlideModel;
import com.example.eventure.R;
import com.example.eventure.adapters.EventCarouselAdapter;
import com.example.eventure.fragments.EventsFragment;
import com.example.eventure.fragments.PasFragment;
import com.example.eventure.model.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

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
        /*
         * Metoda setContentview() veze aktivnost sa layout-om
         * R -> referenca na resources folder tj. na resurse
         * R.layout.activity_home -> pristupamo preko naziva layout-a
         * */
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("EVENTURE");
//
//        }
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextStyle);
        toolbar.setContentInsetStartWithNavigation(70);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Display application icon in the toolbar
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.app_icon);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);

//        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
//        bottomNav.setOnNavigationItemSelectedListener(navListener);

        drawer = findViewById(R.id.drawer_home_layout);
        navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main_home);

        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EventsFragment()).commit();
        

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
//        navController = Navigation.findNavController(this, R.id.fragment_container);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Убедитесь, что стрелка назад отключена
            actionBar.setDisplayHomeAsUpEnabled(false);

            // Устанавливаем иконку "гамбургера" вместо стрелки назад
            actionBar.setHomeAsUpIndicator(R.drawable.ic_humburger);

            // Отключаем функционал кнопки возврата
            actionBar.setHomeButtonEnabled(true);
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
                R.id.nav_favorite_services, R.id.nav_favorite_events, R.id.nav_my_calendar,
                R.id.pas_menu, R.id.events_menu
        ).setOpenableLayout(drawer).build();


        NavigationUI.setupWithNavController(bottomNav, navController);

// Связываем ActionBar с NavController и AppBarConfiguration для поддержки заголовков и кнопки "гамбургера"
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);



//// Связываем BottomNavigationView с NavController
//        NavigationUI.setupWithNavController(bottomNav, navController);
//
//// Связываем ActionBar с NavController и AppBarConfiguration для поддержки заголовков и кнопки "гамбургера"
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);



        // Find profile icon in toolbar
        @SuppressLint("ResourceType") View profileIcon = toolbar.findViewById(R.id.nav_profile); // Replace with your profile icon ID
        profileIcon.setOnClickListener(v -> {
            // Create an Intent to start ProfileActivity
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (getSupportActionBar() != null) {
                // Устанавливаем нужный заголовок
                getSupportActionBar().setTitle("EVENTURE");
            }
        });
//       ImageSlider slider = findViewById(R.id.TopEventsSlider);
//        List<SlideModel> slideModels = new ArrayList<>();
//        slideModels.add(new SlideModel(R.drawable.concert));
//        slideModels.add(new SlideModel(R.drawable.event));
//        slideModels.add(new SlideModel(R.drawable.wedding));
//        slider.setImageList(slideModels,true);
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
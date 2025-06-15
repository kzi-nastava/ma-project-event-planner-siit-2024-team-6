package com.example.eventure.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.ReportAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.ReportDTO;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReportsActivity extends AppCompatActivity {

    private RecyclerView rvReports;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Button btnLoadMore;
    private ReportAdapter reportAdapter;

    private int currentPage = 0;
    private final int pageSize = ClientUtils.PAGE_SIZE;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);  // create this layout similar to activity_comments.xml

        //navigation restriction (only for admin)
        if (!ClientUtils.getAuthService().isLoggedIn()) {
            TextView error = findViewById(R.id.reportsError);
            error.setVisibility(View.VISIBLE);
            TextView reportsTv = findViewById(R.id.tvReportsLabel);
            reportsTv.setVisibility(View.GONE);
            return;
        }else{
            if(!ClientUtils.getAuthService().getRole().equals("ROLE_ADMIN")){
                TextView error = findViewById(R.id.reportsError);
                error.setVisibility(View.VISIBLE);
                TextView reportsTv = findViewById(R.id.tvReportsLabel);
                reportsTv.setVisibility(View.GONE);
                return;
            }
        }

        TextView error = findViewById(R.id.reportsError);
        error.setVisibility(View.GONE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextStyle);
        toolbar.setContentInsetStartWithNavigation(70);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        rvReports = findViewById(R.id.recycler_view_reports);
        btnLoadMore = findViewById(R.id.loadMoreReports);

        reportAdapter = new ReportAdapter(new ReportAdapter.OnItemActionListener() {
            @Override
            public void onSuspend(ReportDTO report) {
                suspendUser(report);
            }

            @Override
            public void onReject(ReportDTO report) {
                rejectReport(report);
            }
        });

        rvReports.setLayoutManager(new LinearLayoutManager(this));
        rvReports.setAdapter(reportAdapter);

        btnLoadMore.setOnClickListener(v -> {
            if (!isLoading && !isLastPage) {
                loadPendingReports(currentPage + 1, pageSize);
            }
        });

        loadPendingReports(0, pageSize);

        DrawerLayout drawer = findViewById(R.id.drawer_reports_layout);
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
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        @SuppressLint("ResourceType") View profileIcon = toolbar.findViewById(R.id.nav_profile);
        profileIcon.setOnClickListener(v -> {
            // Create an Intent to start ProfileActivity
            Intent intent = new Intent(AdminReportsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void loadPendingReports(int page, int size) {
        isLoading = true;
        Call<PagedResponse<ReportDTO>> call = ClientUtils.reportService.getReports(page, size);
        call.enqueue(new Callback<PagedResponse<ReportDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<ReportDTO>> call, Response<PagedResponse<ReportDTO>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<ReportDTO> newReports = response.body().getContent();
                    if (page == 0) {
                        reportAdapter.setReports(newReports);
                    } else {
                        reportAdapter.addReports(newReports);
                    }
                    currentPage = page;
                    if( reportAdapter.getItemCount() == response.body().getTotalElements() ){
                        isLastPage = true;
                    }
                    btnLoadMore.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
                } else {
                    Toast.makeText(AdminReportsActivity.this, "Failed to load reports", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<ReportDTO>> call, Throwable t) {
                isLoading = false;
                Toast.makeText(AdminReportsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void suspendUser(ReportDTO report) {
        Call<Map<String, String>> call = ClientUtils.reportService.approveReport(report.getId());
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminReportsActivity.this, "User suspended", Toast.LENGTH_SHORT).show();
                    loadPendingReports(0, pageSize);
                } else {
                    Toast.makeText(AdminReportsActivity.this, "Failed to suspend user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(AdminReportsActivity.this, "Error suspending user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectReport(ReportDTO report) {
        Call<Map<String, String>> call = ClientUtils.reportService.rejectReport(report.getId());
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminReportsActivity.this, "Report rejected", Toast.LENGTH_SHORT).show();
                    loadPendingReports(0, pageSize);
                } else {
                    Toast.makeText(AdminReportsActivity.this, "Failed to reject report", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(AdminReportsActivity.this, "Error rejecting report: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

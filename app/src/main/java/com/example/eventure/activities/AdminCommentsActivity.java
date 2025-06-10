package com.example.eventure.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.CommentAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.ReactionDTO;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCommentsActivity extends AppCompatActivity {
    private RecyclerView rvComments;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Button btnLoadMore;
    private CommentAdapter commentAdapter;

    private int currentPage = 0;
    private final int pageSize = ClientUtils.PAGE_SIZE;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextStyle);
        toolbar.setContentInsetStartWithNavigation(70);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Comments
        rvComments = findViewById(R.id.recycler_view_comments);
        btnLoadMore = findViewById(R.id.loadMoreComments);
        commentAdapter = new CommentAdapter(new CommentAdapter.OnItemActionListener() {
            @Override
            public void onApprove(ReactionDTO comment) {
                approveComment(comment);
            }

            @Override
            public void onDelete(ReactionDTO comment) {
                deleteComment(comment);
            }
        });
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);
        // Load more
        btnLoadMore.setOnClickListener(v -> {
            if (!isLoading && !isLastPage) {
                loadPendingComments(currentPage + 1, pageSize);
            }
        });
        // loading comments
        loadPendingComments(0, pageSize);

        // Initialize DrawerLayout and NavigationView
        DrawerLayout drawer = findViewById(R.id.drawer_comments_layout);
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
            } else if (id == R.id.nav_admin_manage_reports) {
                startActivity(new Intent(this, AdminReportsActivity.class));
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        @SuppressLint("ResourceType") View profileIcon = toolbar.findViewById(R.id.nav_profile);
        profileIcon.setOnClickListener(v -> {
            // Create an Intent to start ProfileActivity
            Intent intent = new Intent(AdminCommentsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    private void loadPendingComments(int page, int size) {
        isLoading = true;
        Call<PagedResponse<ReactionDTO>> call = ClientUtils.reactionService.getPendingReactions(page, size);
        call.enqueue(new Callback<PagedResponse<ReactionDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<ReactionDTO>> call, Response<PagedResponse<ReactionDTO>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<ReactionDTO> newComments = response.body().getContent();
                    if (page == 0) {
                        commentAdapter.setComments(newComments);
                    } else {
                        commentAdapter.addComments(newComments);
                    }
                    currentPage = page;
                    if( commentAdapter.getItemCount() == response.body().getTotalElements() ){
                        isLastPage = true;
                    }
                    btnLoadMore.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
                } else {
                    Toast.makeText(AdminCommentsActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<ReactionDTO>> call, Throwable t) {
                isLoading = false;
                Toast.makeText(AdminCommentsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approveComment(ReactionDTO comment) {
        Call<ReactionDTO> call = ClientUtils.reactionService.acceptReaction(comment.getId());
        call.enqueue(new Callback<ReactionDTO>() {
            @Override
            public void onResponse(Call<ReactionDTO> call, Response<ReactionDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminCommentsActivity.this, "Comment approved", Toast.LENGTH_SHORT).show();
                    loadPendingComments(0, pageSize);
                } else {
                    Toast.makeText(AdminCommentsActivity.this, "Failed to approve comment", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ReactionDTO> call, Throwable t) {
                Toast.makeText(AdminCommentsActivity.this, "Error approving comment: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteComment(ReactionDTO comment) {
        Call<Void> call = ClientUtils.reactionService.deleteReaction(comment.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminCommentsActivity.this, "Comment deleted", Toast.LENGTH_SHORT).show();
                    loadPendingComments(0, pageSize);
                } else {
                    Toast.makeText(AdminCommentsActivity.this, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminCommentsActivity.this, "Error deleting comment: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

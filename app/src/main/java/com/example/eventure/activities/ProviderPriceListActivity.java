package com.example.eventure.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.PriceListAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.NewPriceListItemDTO;
import com.example.eventure.model.PriceListItem;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderPriceListActivity extends AppCompatActivity implements PriceListAdapter.OnPriceUpdatedListener {

    private RecyclerView recyclerView;
    private PriceListAdapter adapter;
    private List<PriceListItem> priceList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView noListMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleTextStyle);
        toolbar.setContentInsetStartWithNavigation(70);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button btnExportPDF = findViewById(R.id.btnExportPdf);

        btnExportPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Call<ResponseBody> call = ClientUtils.offerService.downloadPriceList();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Save the PDF file
                            boolean written = writeResponseBodyToDisk(response.body(), context);
                            if (written) {
                                openPdfFile(context);
                            } else {
                                Snackbar.make(((View) v.getRootView()), "Failed to save PDF", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(((View) v.getRootView()), "Failed to download PDF", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Snackbar.make(((View) v.getRootView()), "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

        });

        recyclerView = findViewById(R.id.recyclerViewPrices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noListMessage = findViewById(R.id.no_list_message);
        noListMessage.setVisibility(View.GONE);
        loadPriceList();

        DrawerLayout drawer = findViewById(R.id.drawer_price_list_layout);
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
            } else if(id == R.id.nav_my_offers){
                startActivity(new Intent(this,ProviderOffersActivity.class));
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        TextView tvTitle = toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setOnClickListener(v -> {
            Intent intent = new Intent(ProviderPriceListActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        @SuppressLint("ResourceType") View profileIcon = toolbar.findViewById(R.id.nav_profile);
        profileIcon.setOnClickListener(v -> {
            // Create an Intent to start ProfileActivity
            Intent intent = new Intent(ProviderPriceListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void loadPriceList() {
        Call<List<PriceListItem>> call = ClientUtils.offerService.getPriceList();

        call.enqueue(new Callback<List<PriceListItem>>() {
            @Override
            public void onResponse(Call<List<PriceListItem>> call, Response<List<PriceListItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PriceListItem> priceList = response.body();
                    adapter = new PriceListAdapter(ProviderPriceListActivity.this, priceList);
                    adapter.setOnPriceUpdatedListener(ProviderPriceListActivity.this);
                    recyclerView.setAdapter(adapter);
                    if (adapter.getItemCount() == 0) {
                        noListMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noListMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else if (response.code() == 401) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Unauthorized. Please login again.",
                            Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Failed to load price list",
                            Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<PriceListItem>> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Network error: " + t.getMessage(),
                        Snackbar.LENGTH_LONG).show();
            }
        });


    }
    @Override
    public void onPriceUpdated(PriceListItem updatedItem) {
        // Build your DTO from updatedItem
        NewPriceListItemDTO dto = new NewPriceListItemDTO(updatedItem.getOfferPrice(), updatedItem.getOfferDiscountPrice());
        // Call backend to update
        ClientUtils.offerService.updatePrice(updatedItem.getOfferId(), dto).enqueue(new Callback<PriceListItem>() {
            @Override
            public void onResponse(Call<PriceListItem> call, Response<PriceListItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int index = -1;
                    for (int i = 0; i < adapter.getPriceList().size(); i++) {
                        if (adapter.getPriceList().get(i).getOfferId() == updatedItem.getOfferId()) {
                            index = i;
                            break;
                        }
                    }

                    if (index != -1) {
                        adapter.getPriceList().set(index, updatedItem);
                        adapter.notifyItemChanged(index);
                    }
                    Snackbar.make(findViewById(android.R.id.content), "Price updated successfully", Snackbar.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    // Show error message from backend
                    try {
                        String errorBody = response.errorBody().string();
                        Snackbar.make(findViewById(android.R.id.content), "Discount price must be lower than regular.", Snackbar.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Snackbar.make(findViewById(android.R.id.content), "Invalid input", Snackbar.LENGTH_LONG).show();
                    }
                }  else {
                    Snackbar.make(findViewById(android.R.id.content), "Update failed: " + response.message(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PriceListItem> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Network error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private boolean writeResponseBodyToDisk(ResponseBody body, Context context) {
        try {
            File pdfFile = new File(context.getExternalFilesDir(null), "price_list.pdf");
            InputStream inputStream = body.byteStream();
            FileOutputStream outputStream = new FileOutputStream(pdfFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openPdfFile(Context context) {
        File pdfFile = new File(context.getExternalFilesDir(null), "price_list.pdf");

        if (!pdfFile.exists()) {
            Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri pdfUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                pdfFile
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show();
        }
    }


}

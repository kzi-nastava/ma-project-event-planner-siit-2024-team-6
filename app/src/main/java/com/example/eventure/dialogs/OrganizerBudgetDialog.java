package com.example.eventure.dialogs;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.OfferAdapter;
import com.example.eventure.adapters.OrganizerBudgetItemAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.BudgetItemDTO;
import com.example.eventure.dto.NewBudgetDTO;
import com.example.eventure.dto.NewBudgetItemDTO;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.Budget;
import com.example.eventure.model.BudgetItem;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerBudgetDialog extends DialogFragment {

    private int eventId;
    private RecyclerView offerRecyclerView, budgetRecycler;
    private LinearLayout noOffersMessage;
    private OfferAdapter offerAdapter;
    private Integer budgetId;
    private OrganizerBudgetItemAdapter budgetItemAdapter;

    private final List<OfferDTO> matchedOffers = new ArrayList<>();
    private final List<BudgetItem> budgetItems = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();
    private TextView totalValueText;
    private TextView leftValueText;

    private int currentPage = 0;
    private final int pageSize = 8;
    private boolean isLastPage = false;



    public static OrganizerBudgetDialog newInstance(long eventId) {
        OrganizerBudgetDialog dialog = new OrganizerBudgetDialog();
        Bundle args = new Bundle();
        args.putLong("eventId", eventId);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_budget_planning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        eventId = getArguments() != null ? (int) getArguments().getLong("eventId", -1) : -1;
        fetchCategories();
        offerRecyclerView = view.findViewById(R.id.offer_list);
        budgetRecycler = view.findViewById(R.id.budget_items_recycler);
        noOffersMessage = view.findViewById(R.id.no_offers_message);
        totalValueText = view.findViewById(R.id.total_max_amount);
        leftValueText = view.findViewById(R.id.total_spent_amount);


        offerAdapter = new OfferAdapter(matchedOffers, getChildFragmentManager());
        offerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        offerRecyclerView.setAdapter(offerAdapter);
        offerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm != null && lm.findLastCompletelyVisibleItemPosition() == matchedOffers.size() - 1 && !isLastPage) {
                    fetchOffers();
                }
            }
        });


        budgetItemAdapter = new OrganizerBudgetItemAdapter(budgetItems, new OrganizerBudgetItemAdapter.OnBudgetActionListener() {
            @Override
            public void onEdit(BudgetItem item) {
                EditBudgetItemDialog dialog = EditBudgetItemDialog.newInstance(item);
                dialog.setOnBudgetItemUpdatedListener(updatedItem -> {
                    if(updatedItem.getCurrPrice() > updatedItem.getMaxPrice()){
                        Snackbar.make(requireView(), "Maximal amount cannot be lesser than spent amount.", Snackbar.LENGTH_SHORT).show();
                    }else{
                        updateBudgetItem(item, updatedItem.getMaxPrice());
                    }
                });
                dialog.show(getChildFragmentManager(), "EditBudgetDialog");
            }

            @Override
            public void onDelete(BudgetItem item) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Budget Item: "+item.getCategory())
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if(item.getCurrPrice() > 0){
                                Snackbar.make(requireView(), "You have spent money under this category. It cannot be deleted.", Snackbar.LENGTH_SHORT).show();
                            } else{
                                deleteBudgetItem(item);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        budgetRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        budgetRecycler.setAdapter(budgetItemAdapter);

        ImageButton closeButton = view.findViewById(R.id.close_button);
        Button searchButton = view.findViewById(R.id.search_button);
        Button addNewItemButton = view.findViewById(R.id.new_item_button);

        closeButton.setOnClickListener(v -> dismiss());

        searchButton.setOnClickListener(v -> {
            currentPage = 0;
            isLastPage = false;
            matchedOffers.clear();
            fetchOffers();
        });

        addNewItemButton.setOnClickListener(v-> {
            List<String> unusedCategories = new ArrayList<>();
            for (String category : categories) {
                boolean alreadyUsed = false;
                for (BudgetItem item : budgetItems) {
                    if (item.getCategory().equals(category)) {
                        alreadyUsed = true;
                        break;
                    }
                }
                if (!alreadyUsed) {
                    unusedCategories.add(category);
                }
            }
            if(unusedCategories.isEmpty()){
                Snackbar.make(requireView(), "There are no more categories to choose from.", Snackbar.LENGTH_LONG).show();
                return;
            }

            AddBudgetItemDialog dialog = AddBudgetItemDialog.newInstance((ArrayList<String>) unusedCategories);
            dialog.setOnBudgetItemAddedListener(newItem -> {
                addItemToBudget(newItem);
            });
            dialog.show(getChildFragmentManager(), "AddBudgetItemDialog");
        });

        Button recommendationsButton = view.findViewById(R.id.recommendations_button);

        recommendationsButton.setOnClickListener(v -> {
            fetchEventCategoriesAndShowRecommendations();
        });

        // Listen when OfferDetailsDialog is CLOSED (no matter what happened inside)
        getChildFragmentManager().setFragmentResultListener(
                "offer_details_closed", this, (requestKey, bundle) -> {
                    // Refresh budget and offers on close
                    fetchBudget();

                    currentPage = 0;
                    isLastPage = false;
                    matchedOffers.clear();
                    offerAdapter.setOffers(new ArrayList<>()); // optional: clear UI immediately
                    fetchOffers();
                }
        );

        if (eventId != -1) fetchBudget();
    }

    private void fetchBudget() {
        ClientUtils.eventService.getBudgetByEventId(eventId).enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Budget b = response.body();
                    budgetId = b.getId();
                    budgetItems.clear();
                    budgetItems.addAll(b.getBudgetItems());
                    budgetItemAdapter.updateItems(budgetItems);
                    updateBudgetTotalsUI();
                } else {
                    Snackbar.make(requireView(), "Failed to load budget.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
                Snackbar.make(requireView(), "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void fetchOffers() {
        NewBudgetDTO dto = new NewBudgetDTO();
        List<BudgetItemDTO> items = new ArrayList<>();
        for (BudgetItem item : budgetItems) {
            items.add(new BudgetItemDTO(item));
        }
        dto.setBudgetItems(items);

        ClientUtils.offerService.getFilteredOffersByBudget(budgetId, dto, currentPage, pageSize).enqueue(new Callback<PagedResponse<OfferDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<OfferDTO>> call, Response<PagedResponse<OfferDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OfferDTO> newOffers = response.body().getContent();
                    if (newOffers.isEmpty()) {
                        isLastPage = true;
                    }
                    currentPage++;
                    matchedOffers.addAll(newOffers);
                    List<OfferDTO> offers = new ArrayList<>(matchedOffers);
                    offers.addAll(newOffers);
                    displayOffers(offers);
                } else {
                    Snackbar.make(requireView(), "Failed to load offers.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<OfferDTO>> call, Throwable t) {
                Snackbar.make(requireView(), "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void displayOffers(List<OfferDTO> newOffers) {
        if (newOffers.isEmpty()) {
            offerRecyclerView.setVisibility(View.GONE);
            noOffersMessage.setVisibility(View.VISIBLE);
        } else {
            Log.d("OffersBLA", "matchedOffers size: " + matchedOffers.size());
            offerRecyclerView.setVisibility(View.VISIBLE);
            noOffersMessage.setVisibility(View.GONE);
            offerAdapter.setOffers(newOffers);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Optional: Set dialog width
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.95);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void updateBudgetItem(BudgetItem item, double newPrice){
        ClientUtils.eventService.updateBudgetItem(budgetId, item.getId(), newPrice).enqueue(new Callback<BudgetItemDTO>() {
            @Override
            public void onResponse(Call<BudgetItemDTO> call, Response<BudgetItemDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BudgetItemDTO updated = response.body();
                    BudgetItem newItem = new BudgetItem(updated);
                    int index = budgetItems.indexOf(item);
                    if (index != -1) {
                        budgetItems.set(index, newItem);
                        budgetItemAdapter.notifyItemChanged(index);
                        budgetItemAdapter.updateItems(budgetItems);
                        updateBudgetTotalsUI();
                        Snackbar.make(requireView(), "Budget item updated!", Snackbar.LENGTH_SHORT).show();
                    }else{
                        Snackbar.make(requireView(), "Unexpected error! Try refreshing this page.", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(requireView(), "Update failed", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BudgetItemDTO> call, Throwable t) {
                Snackbar.make(requireView(), "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void deleteBudgetItem(BudgetItem item){
        ClientUtils.eventService.deleteBudgetItem(budgetId, item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    budgetItems.remove(item);
                    budgetItemAdapter.updateItems(budgetItems);
                    updateBudgetTotalsUI();
                    Snackbar.make(requireView(), "Budget item deleted successfully!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(requireView(), "Deletion failed", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Snackbar.make(requireView(), "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void updateBudgetTotalsUI() {
        int total = 0;
        int spent = 0;

        for (BudgetItem item : budgetItems) {
            total += item.getMaxPrice();
            spent += item.getCurrPrice();
        }

        int left = total - spent;

        totalValueText.setText("Total: $" + total);
        leftValueText.setText("Left: $" + left);
    }

    private void fetchCategories(){
        ClientUtils.categoryService.getCategoryNames().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    // Use categories to populate spinner or UI
                } else if (response.code() == 204) {
                    // No categories found
                } else if (response.code() == 401) {
                    // Unauthorized - handle auth failure
                } else {
                    // Other errors
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // Handle network or other errors
            }
        });
    }

    private void addItemToBudget(BudgetItem item){
        NewBudgetItemDTO dto = new NewBudgetItemDTO(item.getCategory(), item.getMaxPrice(), 0);

        Call<BudgetItemDTO> call = ClientUtils.eventService.addItemToBudget(budgetId, dto);

        call.enqueue(new Callback<BudgetItemDTO>() {
            @Override
            public void onResponse(Call<BudgetItemDTO> call, Response<BudgetItemDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BudgetItemDTO addedItem = response.body();
                    BudgetItem newItem = new BudgetItem(addedItem);
                    budgetItems.add(newItem);
                    budgetItemAdapter.notifyItemInserted(budgetItems.size() - 1);
                    updateBudgetTotalsUI();
                    budgetItemAdapter.updateItems(budgetItems);
                    Snackbar.make(requireView(), "Budget item created!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(requireView(), "Creation failed", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BudgetItemDTO> call, Throwable t) {
                Snackbar.make(requireView(), "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void fetchEventCategoriesAndShowRecommendations() {

        ClientUtils.eventService.getEventCategories(eventId).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> recommended = response.body();
                    List<String> recs = new ArrayList<>();
                    for(String c : recommended) {
                        boolean exists = false;
                        for(BudgetItem i: budgetItems){
                            if(i.getCategory().equals(c)){
                                exists = true;
                                break;
                            }
                        }
                        if (!exists){
                            recs.add(c);
                        }
                    }
                    if(recs.isEmpty()){
                        Snackbar.make(requireView(), "All recommended categories are already added.", Snackbar.LENGTH_SHORT).show();

                    }else{
                        BudgetRecommendationsDialog dialog = BudgetRecommendationsDialog.newInstance(recs);

                        dialog.setOnCategoryChosenListener(newItem -> {
                            addItemToBudget(newItem);
                        });

                        dialog.show(getChildFragmentManager(), "BudgetRecommendationsDialog");
                    }
                } else if (response.code() == 204) {
                    Snackbar.make(requireView(), "No categories found for this event.", Snackbar.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    Snackbar.make(requireView(), "Unauthorized access.", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(requireView(), "Failed to load categories.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Snackbar.make(requireView(), "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }


}

package com.example.eventure.dialogs;

import android.app.AlertDialog;
import android.os.Bundle;
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
    private TextView totalValueText;
    private TextView leftValueText;


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

        offerRecyclerView = view.findViewById(R.id.offer_list);
        budgetRecycler = view.findViewById(R.id.budget_items_recycler);
        noOffersMessage = view.findViewById(R.id.no_offers_message);
        totalValueText = view.findViewById(R.id.total_max_amount);
        leftValueText = view.findViewById(R.id.total_spent_amount);


        offerAdapter = new OfferAdapter(matchedOffers, getChildFragmentManager());
        offerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        offerRecyclerView.setAdapter(offerAdapter);

        budgetItemAdapter = new OrganizerBudgetItemAdapter(budgetItems, new OrganizerBudgetItemAdapter.OnBudgetActionListener() {
            @Override
            public void onEdit(BudgetItem item) {
                EditBudgetItemDialog dialog = EditBudgetItemDialog.newInstance(item);
                dialog.setOnBudgetItemUpdatedListener(updatedItem -> {
                    if(updatedItem.getCurrPrice() > updatedItem.getMaxPrice()){
                        Snackbar.make(requireView(), "Maximal amount cannot be lesser than spent amount.", Snackbar.LENGTH_SHORT).show();
                    }else{
                        updateBudget(item, updatedItem);
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
                                Snackbar.make(requireView(), "Item deleted", Snackbar.LENGTH_SHORT).show();
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

        closeButton.setOnClickListener(v -> dismiss());

        searchButton.setOnClickListener(v -> fetchOffers());

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

        ClientUtils.offerService.getFilteredOffersByBudget(dto).enqueue(new Callback<PagedResponse<OfferDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<OfferDTO>> call, Response<PagedResponse<OfferDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    matchedOffers.clear();
                    matchedOffers.addAll(response.body().getContent());
                    displayOffers();
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

    private void displayOffers() {
        if (matchedOffers.isEmpty()) {
            offerRecyclerView.setVisibility(View.GONE);
            noOffersMessage.setVisibility(View.VISIBLE);
        } else {
            offerRecyclerView.setVisibility(View.VISIBLE);
            noOffersMessage.setVisibility(View.GONE);
            offerAdapter.setOffers(matchedOffers);
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

    private void updateBudget(BudgetItem item, BudgetItem updatedItem){
        NewBudgetDTO dto = new NewBudgetDTO();
        List<BudgetItemDTO> items = new ArrayList<>();
        int index = budgetItems.indexOf(item);
        if (index != -1) {
            budgetItems.set(index, updatedItem);
            budgetItemAdapter.notifyItemChanged(index);
        }else{
            Snackbar.make(requireView(), "Update failed", Snackbar.LENGTH_SHORT).show();
            return;
        }
        for (BudgetItem i: budgetItems){
            items.add(new BudgetItemDTO(i));
        }
        dto.setBudgetItems(items);
        ClientUtils.eventService.updateBudget(budgetId, dto).enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Budget updatedBudget = response.body();
                    budgetItems.clear();
                    budgetItems.addAll(updatedBudget.getBudgetItems());
                    budgetItemAdapter.updateItems(budgetItems);
                    updateBudgetTotalsUI();
                    Snackbar.make(requireView(), "Budget updated!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(requireView(), "Update failed", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
                Snackbar.make(requireView(), "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void deleteBudgetItem(BudgetItem item){
        NewBudgetDTO dto = new NewBudgetDTO();
        List<BudgetItemDTO> items = new ArrayList<>();
        budgetItems.remove(item);
        for (BudgetItem i: this.budgetItems){
            items.add(new BudgetItemDTO(i));
        }
        dto.setBudgetItems(items);
        ClientUtils.eventService.updateBudget(budgetId, dto).enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Budget updatedBudget = response.body();
                    budgetItemAdapter.updateItems(budgetItems);
                    updateBudgetTotalsUI();
                    Snackbar.make(requireView(), "Budget updated!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(requireView(), "Update failed", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
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

}

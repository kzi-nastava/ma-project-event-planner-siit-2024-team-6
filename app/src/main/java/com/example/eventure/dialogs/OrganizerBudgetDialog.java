package com.example.eventure.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
    private OrganizerBudgetItemAdapter budgetItemAdapter;

    private final List<OfferDTO> matchedOffers = new ArrayList<>();
    private final List<BudgetItem> budgetItems = new ArrayList<>();

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

        offerAdapter = new OfferAdapter(matchedOffers, getChildFragmentManager());
        offerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        offerRecyclerView.setAdapter(offerAdapter);

        budgetItemAdapter = new OrganizerBudgetItemAdapter(budgetItems, new OrganizerBudgetItemAdapter.OnBudgetActionListener() {
            @Override
            public void onEdit(BudgetItem item) {
                // TODO: Handle edit
            }

            @Override
            public void onDelete(BudgetItem item) {
                // TODO: Handle delete
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
                    budgetItems.clear();
                    budgetItems.addAll(b.getBudgetItems());
                    budgetItemAdapter.updateItems(budgetItems);
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
}

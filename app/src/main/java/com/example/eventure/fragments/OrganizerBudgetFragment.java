package com.example.eventure.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventure.R;
import com.example.eventure.adapters.OfferAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.BudgetItemDTO;
import com.example.eventure.dto.NewBudgetDTO;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.BudgetItem;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerBudgetFragment  extends Fragment {

    private RecyclerView offerRecyclerView;
    private LinearLayout noOffersMessage;
    private OfferAdapter offerAdapter;

    private List<OfferDTO> matchedOffers = new ArrayList<>();
    private List<BudgetItem> budgetItems = new ArrayList<>(); // Replace with real data

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget_planning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        offerRecyclerView = view.findViewById(R.id.offer_list);
        noOffersMessage = view.findViewById(R.id.no_offers_message);
        ImageButton closeButton = view.findViewById(R.id.close_button);
        Button searchButton = view.findViewById(R.id.search_button);

        offerAdapter = new OfferAdapter(matchedOffers, getChildFragmentManager());
        offerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        offerRecyclerView.setAdapter(offerAdapter);

        closeButton.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager().popBackStack());

        searchButton.setOnClickListener(v -> {
            findOffersForBudget(budgetItems); // Dummy logic
            displayOffers();
        });

        displayOffers();
    }

    private void findOffersForBudget(List<BudgetItem> budgetItems) {
        NewBudgetDTO budgetDTO = new NewBudgetDTO();
        List<BudgetItemDTO> itemDTOS = new ArrayList<>();
        for (BudgetItem item: budgetItems){
            itemDTOS.add(new BudgetItemDTO(item));
        }
        budgetDTO.setBudgetItems(itemDTOS);
        Call<PagedResponse<OfferDTO>> call = ClientUtils.offerService.getFilteredOffersByBudget(budgetDTO);
        call.enqueue(new Callback<PagedResponse<OfferDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<OfferDTO>> call, Response<PagedResponse<OfferDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OfferDTO> offers = response.body().getContent();
                    matchedOffers = offers;
                } else {
                    Snackbar.make(requireView(), "Something went wrong. Please try again.", Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", v -> {})
                            .show();

                }
            }

            @Override
            public void onFailure(Call<PagedResponse<OfferDTO>> call, Throwable t) {
                Snackbar.make(requireView(), "An error has occurred.", Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", v -> {})
                        .show();

            }
        });
    }

    private void displayOffers() {
        if (matchedOffers == null || matchedOffers.isEmpty()) {
            offerRecyclerView.setVisibility(View.GONE);
            noOffersMessage.setVisibility(View.VISIBLE);
        } else {
            noOffersMessage.setVisibility(View.GONE);
            offerRecyclerView.setVisibility(View.VISIBLE);
            offerAdapter.setOffers(matchedOffers); // Ensure your adapter has this method
        }
    }
}

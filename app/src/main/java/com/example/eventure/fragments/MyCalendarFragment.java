package com.example.eventure.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventure.R;
import com.example.eventure.adapters.CalendarAdapter;
import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dialogs.EventDetailsDialog;
import com.example.eventure.dialogs.OfferDetailsDialog;
import com.example.eventure.dto.CalendarItemDTO;
import com.example.eventure.model.Event;
import com.example.eventure.model.Offer;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.ViewContainer;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyCalendarFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView eventRecycler;
    private Map<LocalDate, List<CalendarItemDTO>> itemsByDate = new HashMap<>();
    private List<CalendarItemDTO> selectedItems = new ArrayList<>();
    private CalendarAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_calendar, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        eventRecycler = view.findViewById(R.id.eventRecycler);
        adapter = new CalendarAdapter(selectedItems, this::onItemClick);
        eventRecycler.setAdapter(adapter);
        eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchCalendarItems();
        return view;
    }

    private void fetchCalendarItems() {
        ClientUtils.calendarService.getCalendarItems().enqueue(new Callback<List<CalendarItemDTO>>() {
            @Override
            public void onResponse(Call<List<CalendarItemDTO>> call, Response<List<CalendarItemDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (CalendarItemDTO item : response.body()) {
                        itemsByDate.computeIfAbsent(item.getDate().toLocalDate(), d -> new ArrayList<>()).add(item);
                    }
                    initCalendar();
                }
            }

            @Override
            public void onFailure(Call<List<CalendarItemDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load calendar items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCalendar() {
        calendarView.setup(
                YearMonth.from(LocalDate.now().minusMonths(6)),
                YearMonth.from(LocalDate.now().plusMonths(6)),
                DayOfWeek.MONDAY
        );
        calendarView.scrollToDate(LocalDate.now());

        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, @NonNull CalendarDay day) {
                container.textView.setText(String.valueOf(day.getDate().getDayOfMonth()));
                container.getView().setOnClickListener(v -> onDateSelected(day.getDate()));
            }
        });

    }

    private void onDateSelected(LocalDate date) {
        selectedItems.clear();
        List<CalendarItemDTO> items = itemsByDate.get(date);
        if (items != null) selectedItems.addAll(items);
        adapter.notifyDataSetChanged();
    }

    private void onItemClick(CalendarItemDTO item) {
        if ("MyService".equals(item.getType())) {
            Offer o = ClientUtils.offerService.getById(item.getId());
            OfferDetailsDialog.newInstance(o)
                    .show(getParentFragmentManager(), "offer_details");
        } else {
            Event e = ClientUtils.eventService.getById(item.getId());
            EventDetailsDialog.newInstance(e)
                    .show(getParentFragmentManager(), "event_details");
        }
    }

    class DayViewContainer extends ViewContainer {
        final TextView textView;

        public DayViewContainer(View view) {
            super(view);
            textView = view.findViewById(R.id.calendarDayText);
        }
    }
}

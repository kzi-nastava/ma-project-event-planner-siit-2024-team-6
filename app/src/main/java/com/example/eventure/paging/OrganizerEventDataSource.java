package com.example.eventure.paging;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.EventDTO;
import com.example.eventure.model.Event;
import com.example.eventure.model.PagedResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerEventDataSource extends PageKeyedDataSource<Integer, Event> {

    private static final int FIRST_PAGE = 0;
    private final int pageSize;
    private final String searchQuery;

    public OrganizerEventDataSource(int pageSize, String searchQuery) {
        this.pageSize = pageSize;
        this.searchQuery = searchQuery;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, Event> callback) {

        Log.d("OrganizerEventDataSource", "Loading initial events...");

        try {
            Response<PagedResponse<EventDTO>> response = ClientUtils.eventService
                    .getPagedEvents(FIRST_PAGE, pageSize, searchQuery)
                    .execute(); // <-- ВАЖНО: блокирующий вызов

            if (response.isSuccessful() && response.body() != null) {
                List<Event> events = convert(response.body().getContent());
                Log.d("OrganizerEventDataSource", "Fetched " + events.size() + " events from backend");
                callback.onResult(events, null, FIRST_PAGE + 1);
            } else {
                Log.e("OrganizerEventDataSource", "Initial load failed: " + response.code());
            }
        } catch (IOException e) {
            Log.e("OrganizerEventDataSource", "Initial load error: " + e.getMessage(), e);
        }
    }



    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Event> callback) {
        // Not used for forward-only paging
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Event> callback) {
        Log.d("OrganizerEventDataSource", "Loading next page: " + params.key);
        ClientUtils.eventService.getPagedEvents(params.key, pageSize, searchQuery)
                .enqueue(new Callback<PagedResponse<EventDTO>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<EventDTO>> call, Response<PagedResponse<EventDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Event> events = convert(response.body().getContent());
                            callback.onResult(events, params.key + 1);
                        } else {
                            Log.e("OrganizerEventDataSource", "Load after failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<EventDTO>> call, Throwable t) {
                        Log.e("OrganizerEventDataSource", "Load after error: " + t.getMessage());
                    }
                });
    }

    private List<Event> convert(List<EventDTO> dtoList) {
        List<Event> eventList = new ArrayList<>();
        for (EventDTO dto : dtoList) {
            eventList.add(new Event(dto));
        }
        Log.d("CONVERT", "Converting DTO to Event, count: " + dtoList.size());
        for (EventDTO dto : dtoList) {
            Log.d("CONVERT", "DTO name: " + dto.getName());
        }
        for (Event e : eventList) {
            Log.d("WITHOUTCONVERT", "Event name: " + e.getName());
        }
        return eventList;
    }
}

package com.example.eventure.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.example.eventure.clients.ClientUtils;
import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.model.EventType;
import com.example.eventure.model.PagedResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEventTypeDataSource extends PageKeyedDataSource<Integer, EventType> {

    private static final int FIRST_PAGE = 0;
    private final int pageSize;

    public AdminEventTypeDataSource(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, EventType> callback) {
        Log.d("AdminEventTypeDataSource", "Loading initial event types...");

        try {
            Response<PagedResponse<EventTypeDTO>> response = ClientUtils.adminService
                    .getPagedEventTypes(FIRST_PAGE, pageSize) // ✅ только два аргумента
                    .execute();

            if (response.isSuccessful() && response.body() != null) {
                List<EventType> eventTypes = convert(response.body().getContent());
                callback.onResult(eventTypes, null, FIRST_PAGE + 1);
            } else {
                Log.e("AdminEventTypeDataSource", "Initial load failed: " + response.code());
            }
        } catch (IOException e) {
            Log.e("AdminEventTypeDataSource", "Initial load error: " + e.getMessage(), e);
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, EventType> callback) {
        // Not used
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<Integer, EventType> callback) {
        Log.d("AdminEventTypeDataSource", "Loading next page: " + params.key);
        ClientUtils.adminService.getPagedEventTypes(params.key, pageSize)
                .enqueue(new Callback<PagedResponse<EventTypeDTO>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<EventTypeDTO>> call, Response<PagedResponse<EventTypeDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<EventType> eventTypes = convert(response.body().getContent());
                            callback.onResult(eventTypes, params.key + 1);
                        } else {
                            Log.e("AdminEventTypeDataSource", "Load after failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<EventTypeDTO>> call, Throwable t) {
                        Log.e("AdminEventTypeDataSource", "Load after error: " + t.getMessage());
                    }
                });
    }

    private List<EventType> convert(List<EventTypeDTO> dtoList) {
        List<EventType> eventTypeList = new ArrayList<>();
        for (EventTypeDTO dto : dtoList) {
            eventTypeList.add(new EventType(dto));
        }
        return eventTypeList;
    }
}

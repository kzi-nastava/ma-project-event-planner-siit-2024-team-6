package com.example.eventure.clients;

import com.example.eventure.dto.EventDTO;
import com.example.eventure.model.PagedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface EventService {
    @GET(ClientUtils.TOP_FIVE_EVENTS)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: true"
    })
    Call<List<EventDTO>> getTopFive();

    @GET(ClientUtils.ALL_EVENTS)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: true"
    })
    Call<List<EventDTO>> getAll();
    @GET(ClientUtils.ALL_EVENTS_PAGED)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: true"
    })
    Call<PagedResponse<EventDTO>> getPagedEvents(@Query("page") int page, @Query("size") int size);
    @GET(ClientUtils.FILTERED_EVENTS)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: true"
    })
    Call<PagedResponse<EventDTO>> getFilteredEvents(
            @Query("name") String name,
            @Query("description") String description,
            @Query("place") String place,
            @Query("eventType") String eventType,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
}

package com.example.eventure.clients;

import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.model.EventType;
import com.example.eventure.model.Offer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface EventTypeService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("events/{name}/event-type")
    Call<EventType> findEventType(@Path("name") String name);
    @GET("events/event-types")
    Call<List<EventType>> findAll();
    @GET("providers/event-types")
    Call<List<String>> findAllNames();
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(ClientUtils.ALL_EVENT_TYPES)
    Call<List<EventTypeDTO>> getAll();

}

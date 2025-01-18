package com.example.eventure.clients;

import com.example.eventure.dto.EventDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EventService {
    @GET(ClientUtils.TOP_FIVE_EVENTS)
    Call<List<EventDTO>> getTopFive();

    @GET(ClientUtils.ALL_EVENTS)
    Call<List<EventDTO>> getAll();

}

package com.example.eventure.clients;

import com.example.eventure.dto.CalendarItemDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CalendarService {

    @GET("calendar/items")
    Call<List<CalendarItemDTO>> getCalendarItems();
}

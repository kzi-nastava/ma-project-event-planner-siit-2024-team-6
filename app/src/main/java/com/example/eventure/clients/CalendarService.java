package com.example.eventure.clients;

import com.example.eventure.dto.CalendarItemDTO;

import java.util.List;

import retrofit2.Call;

public interface CalendarService {
    Call<List<CalendarItemDTO>> getCalendarItems();
}

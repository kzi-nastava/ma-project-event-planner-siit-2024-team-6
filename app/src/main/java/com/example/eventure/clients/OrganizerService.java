package com.example.eventure.clients;

import com.example.eventure.dto.EventDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface OrganizerService {

    @GET(ClientUtils.ORGANIZER_FUTURE_EVENTS)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    Call<List<EventDTO>> getFutureEventsForOrganizer();
    @GET("organizers/events/{eventId}/getAgendaPDF")
    Call<okhttp3.ResponseBody> getAgendaPdf(@retrofit2.http.Path("eventId") int eventId);

}

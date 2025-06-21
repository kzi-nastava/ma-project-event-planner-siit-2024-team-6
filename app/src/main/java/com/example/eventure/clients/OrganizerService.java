package com.example.eventure.clients;

import com.example.eventure.dto.EventDTO;
import com.example.eventure.dto.NewEventDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    @POST("organizers/events")
    Call<EventDTO> createEvent(
            @Body NewEventDTO newEvent
    );
    @PUT("organizers/events/{eventId}")
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    Call<EventDTO> updateEvent(
            @Path("eventId") int eventId,
            @Body EventDTO updatedEvent
    );
    @DELETE("organizers/events/{eventId}")
    Call<Void> deleteEvent(@Path("eventId") int eventId);

}

package com.example.eventure.clients;

import com.example.eventure.dto.EventDTO;
import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.model.PagedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
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
    Call<PagedResponse<EventDTO>> getPagedEvents(@Query("page") int page, @Query("size") int size, @Query("sortDir") String sortDir);
    @GET("organizers/paged-events")
    Call<PagedResponse<EventDTO>> getPagedEventsByO(@Query("page") int page, @Query("size") int size, @Query("sortDir") String sortDir);


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
            @Query("pageSize") int pageSize,
            @Query("sortDir") String sortDir
    );
    @POST("events/{eventId}/favorite")
    Call<Void> addEventToFavorites(@retrofit2.http.Path("eventId") int eventId);

    @DELETE("events/{eventId}/favorite")
    Call<Void> removeEventFromFavorites(@retrofit2.http.Path("eventId") int eventId);

    @GET("events/{eventId}/is-favorited")
    Call<Boolean> isEventFavorited(@retrofit2.http.Path("eventId") int eventId);
    @GET("events/unPagedFavorites")
    Call<List<EventDTO>> getUnpagedFavorites();

    @GET("events/favorites")
    Call<PagedResponse<EventDTO>> getPagedFavorites(@Query("page") int page, @Query("size") int size);
    @POST("events/{eventId}/participate")
    Call<Void> participate(@retrofit2.http.Path("eventId") int eventId);

    @DELETE("events/{eventId}/participate")
    Call<Void> removeParticipation(@retrofit2.http.Path("eventId") int eventId);
    @GET("events/participated")
    Call<List<EventDTO>> getParticipatedEvents();
    @GET("events/{eventId}/is-participating")
    Call<Boolean> isParticipating(@Path("eventId") int eventId);
    @GET("events/{eventId}/getInfoPDF")
    Call<okhttp3.ResponseBody> getInfoPdf(@retrofit2.http.Path("eventId") int eventId);
    @GET("events/{eventId}/getEventStatisticsPDF")
    Call<okhttp3.ResponseBody> getStatisticsPdf(@retrofit2.http.Path("eventId") int eventId);
    @GET("events/event-types")
    Call<List<String>> getAllEventTypes();

    @GET("events/{name}/event-type")
    Call<EventTypeDTO> getEventTypeByName(@retrofit2.http.Path("name") String name);

    @GET("events/{categoryId}/event-types-by-category")
    Call<List<EventTypeDTO>> getEventTypesByCategory(@retrofit2.http.Path("categoryId") int categoryId);

    @GET("events/{categoryName}/event-types-by-category-name")
    Call<List<EventTypeDTO>> getEventTypesByCategoryName(@retrofit2.http.Path("categoryName") String categoryName);

}

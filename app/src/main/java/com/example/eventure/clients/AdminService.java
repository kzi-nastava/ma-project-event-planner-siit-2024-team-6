package com.example.eventure.clients;

import com.example.eventure.dto.EventTypeDTO;
import com.example.eventure.model.EventType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AdminService {
//    @Headers({
//            "User-Agent: Mobile-Android",
//            "Content-Type:application/json"
//    })
//    @GET("events/{name}/event-type")
//    Call<EventType> findEventType(@Path("name") String name);
//    @GET("events/event-types")
//    Call<List<EventType>> findAll();
//    @GET("providers/event-types")
//    Call<List<String>> findAllNames();
    @GET("admins/event-types")
    Call<List<EventTypeDTO>> getAllEventTypesPaged();

    @POST("admins/event-types")
    Call<EventTypeDTO> createEventType(EventTypeDTO newEventType);
    @PUT("admins/event-types/{id}")
    Call<EventTypeDTO> updateEventType(@Path("id") int id, EventTypeDTO dto);

    @PUT("admins/event-types/{id}/change-status")
    Call<Void> deleteEventType(@Path("id") Integer id);

}

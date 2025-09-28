package com.example.eventure.clients;

import com.example.eventure.dto.NewNotificationDTO;
import com.example.eventure.dto.NotificationDTO;
import com.example.eventure.model.PagedResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {

    @POST(ClientUtils.NOTIFICATIONS)
    Call<NotificationDTO> addNotification(@Body NewNotificationDTO newNotificationDTO);

    @GET(ClientUtils.RECEIVER_NOTIFICATIONS)
    Call<PagedResponse<NotificationDTO>> getByReceiver(
            @Path("receiverId") int receiverId,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("sortDir") String sortDir
    );

    @PUT(ClientUtils.MUTE)
    Call<Void> toggleMute(
            @Path("userId") Integer userId,
            @Query("mute") boolean mute
    );
}

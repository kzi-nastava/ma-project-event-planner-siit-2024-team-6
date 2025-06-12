package com.example.eventure.clients;

import com.example.eventure.dto.NewReservationDTO;
import com.example.eventure.dto.ReservationDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReservationService {

    @POST(ClientUtils.ADD_RESERVATION)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    Call<ReservationDTO> addReservation(@Body NewReservationDTO newReservationDTO);

    @GET("reservations/{offerId}/reserved")
    Call<Boolean> isOfferReservedByUser(
            @Path("offerId") int offerId
    );
}

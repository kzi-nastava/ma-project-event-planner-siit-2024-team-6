package com.example.eventure.clients;

import com.example.eventure.dto.NewReservationDTO;
import com.example.eventure.dto.ReservationDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ReservationService {

    @POST(ClientUtils.ADD_RESERVATION)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    Call<ReservationDTO> addReservation(@Body NewReservationDTO newReservationDTO);
}

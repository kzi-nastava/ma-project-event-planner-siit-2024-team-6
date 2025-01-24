package com.example.eventure.clients;


import com.example.eventure.dto.LoginDTO;
import com.example.eventure.dto.LoginResponseDTO;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(ClientUtils.LOGIN)
    Call<LoginResponseDTO> login(@Body LoginDTO tag);
}

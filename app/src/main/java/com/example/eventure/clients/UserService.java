package com.example.eventure.clients;

import com.example.eventure.dto.EventDTO;
import com.example.eventure.dto.LoginDTO;
import com.example.eventure.dto.PasswordChangeDTO;
import com.example.eventure.dto.QuickRegistrationDTO;
import com.example.eventure.dto.RegistrationRequestDTO;
import com.example.eventure.dto.UserDTO;
import com.example.eventure.model.PagedResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    // POST /api/users
    @POST("users")
    Call<UserDTO> registerUser(@Body RegistrationRequestDTO registrationRequestDTO);

//    // POST /api/users/quick-register
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: true"
    })
    @POST("users/quick-register")
    Call<UserDTO> quickRegister(@Body QuickRegistrationDTO quickRegistrationDTO);

    // POST /api/users/login
    @POST("users/login")
    Call<Map<String, String>> login(@Body LoginDTO loginDTO); // Токен будет в поле "token" в ответе

    // GET /api/users/profile
    @GET("users/profile")
    Call<UserDTO> getProfile(); // токен автоматически добавляется

    // PUT /api/users/profile
    @PUT("users/profile")
    Call<UserDTO> updateProfile(@Body Object updatedUser); // можно заменить Object на Map<String, Object>

    // PUT /api/users/profile/password-change
    @PUT("users/profile/password-change")
    Call<String> changePassword(@Body PasswordChangeDTO passwordChangeDTO);

    // DELETE /api/users/profile
    @DELETE("users/profile")
    Call<UserDTO> deleteAccount();

    // GET /api/users
    @GET("users")
    Call<List<UserDTO>> getAllUsers();

    // PUT /api/users/{id}/role?newRole=...
    @PUT("users/{id}/role")
    Call<String> updateRole(@Path("id") int id, @retrofit2.http.Query("newRole") String newRole);
    @GET("events/favorites")
    Call<PagedResponse<EventDTO>> getPagedFavorites(
            @Query("page") int page,
            @Query("size") int size
    );
}
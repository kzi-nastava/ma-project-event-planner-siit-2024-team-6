package com.example.eventure.clients;

import com.example.eventure.dto.NewReactionDTO;
import com.example.eventure.dto.ReactionDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ReactionService {
    @POST(ClientUtils.ADD_REACTION)
    Call<ReactionDTO> addReaction(
            @Body NewReactionDTO reaction
    );
}

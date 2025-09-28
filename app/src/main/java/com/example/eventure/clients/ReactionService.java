package com.example.eventure.clients;

import com.example.eventure.dto.NewReactionDTO;
import com.example.eventure.dto.ReactionDTO;
import com.example.eventure.model.PagedResponse;

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
import retrofit2.http.Query;

public interface ReactionService {
    @POST(ClientUtils.ADD_REACTION)
    Call<ReactionDTO> addReaction(
            @Body NewReactionDTO reaction
    );

    @GET(ClientUtils.PENDING_REACTIONS)
    Call<PagedResponse<ReactionDTO>> getPendingReactions(@Query("page") int page, @Query("size") int size);

    @DELETE(ClientUtils.REACTIONS + "{id}")
    Call<Void> deleteReaction(@Path("id") Integer id);

    @PUT(ClientUtils.REACTIONS + "{id}/accept")
    Call<ReactionDTO> acceptReaction(@Path("id") Integer id);

    @GET("reactions/provider/{id}")
    Call<PagedResponse<ReactionDTO>> getProviderReactions(
            @Path("id") int providerId,
            @Query("page") int page,
            @Query("size") int size
    );

}

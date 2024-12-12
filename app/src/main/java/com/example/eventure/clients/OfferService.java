package com.example.eventure.clients;

import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.Offer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OfferService {
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("providers/{id}/my-services")
    Call<List<Offer>> getProviderServices(@Path("id") int id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("providers/{providerId}/{offerId}")
    Call<Offer> editProviderService(@Path("providerId") int pId, @Path("offerId") int oId, @Body OfferDTO offer);

    @POST("providers/{providerId}")
    Call<Offer> createProviderService(@Path("providerId") int pId, @Body OfferDTO offer);


}

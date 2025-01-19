package com.example.eventure.clients;

import com.example.eventure.dto.EventDTO;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OfferService {

    @GET(ClientUtils.TOP_FIVE_OFFERS)
    Call<List<OfferDTO>> getTopFive();

    @GET(ClientUtils.ALL_OFFERS)
    Call<List<OfferDTO>> getAll();

    @GET(ClientUtils.ALL_OFFERS_PAGED)
    Call<PagedResponse<OfferDTO>> getPagedOffers(@Query("page") int page, @Query("size") int size);
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("providers/{id}/my-services")
    Call<PagedResponse<Offer>> getProviderServices(@Path("id") int id, @Query("page") int page, @Query("size") int size);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("providers/{providerId}/{offerId}")
    Call<Offer> editProviderService(@Path("providerId") int pId, @Path("offerId") int oId, @Body OfferDTO offer);

    @POST("providers/{providerId}")
    Call<Offer> createProviderService(@Path("providerId") int pId, @Body OfferDTO offer);

    @DELETE("providers/{offerId}")
    Call<Void> deleteProviderService(@Path("offerId") int id);

    @GET("providers/{providerId}/search")
    Call<PagedResponse<Offer>> getSearchedService(@Path("providerId") int id, @Query("name") String name,  @Query("page") int page, @Query("size") int size);
    @GET("providers/{providerId}/services-filter")
    Call<PagedResponse<Offer>> getFilteredServices(
            @Path("providerId") int providerId,
            @Query("categories") List<String> categories,
            @Query("eventTypes") List<String> eventTypes,
            @Query("isAvailable") Boolean isAvailable,
            @Query("price") Double price,
            @Query("page") int page,
            @Query("size") int size
    );

}

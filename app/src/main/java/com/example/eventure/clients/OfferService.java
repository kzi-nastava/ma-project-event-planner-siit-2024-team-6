package com.example.eventure.clients;

import com.example.eventure.dto.NewOfferDTO;
import com.example.eventure.dto.NewPriceListItemDTO;
import com.example.eventure.dto.OfferDTO;
import com.example.eventure.dto.ProviderDTO;
import com.example.eventure.dto.UserDTO;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;
import com.example.eventure.model.PriceListItem;

import java.util.List;

import okhttp3.ResponseBody;
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

public interface OfferService {

    @GET(ClientUtils.TOP_FIVE_OFFERS)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: true"
    })
    Call<List<OfferDTO>> getTopFive();

    @GET(ClientUtils.PRICE_LIST)
    Call<List<PriceListItem>> getPriceList();
    @PUT(ClientUtils.UPDATE_PRICE_LIST)
    Call<PriceListItem> updatePrice(
            @Path("id") int id,
            @Body NewPriceListItemDTO dto
    );
    @GET(ClientUtils.EXPORT_PRICE_LIST)
    Call<ResponseBody> downloadPriceList();

    @POST("offers/{offerId}/buy")
    Call<Void> buyOffer(
            @Path("offerId") int offerId,
            @Query("eventId") int eventId
    );
    @GET(ClientUtils.ALL_OFFERS)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: true"
    })
    Call<List<OfferDTO>> getAll();

    @GET(ClientUtils.ALL_OFFERS_PAGED)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: true"
    })
    Call<PagedResponse<OfferDTO>> getPagedOffers(@Query("page") int page, @Query("size") int size);
    @GET(ClientUtils.ACCEPTED_OFFERS)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: true"
    })
    Call<PagedResponse<OfferDTO>> getAcceptedOffers(@Query("page") int page, @Query("size") int size, @Query("sortDir") String sortDir);
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
    @PUT("providers/{offerId}")
    Call<Offer> editProviderService( @Path("offerId") int oId, @Body NewOfferDTO offer);

    @GET("offers/{offerId}/provider")
    Call<ProviderDTO> getProviderByOfferId(@Path("offerId") int offerId);

    @POST("offers/{offerId}/favourite")
    Call<Void> addOfferToFavourites(
            @Path("offerId") int offerId
    );

    @DELETE("offers/{offerId}/favourite")
    Call<Void> removeOfferFromFavourites(
            @Path("offerId") int offerId
    );
    @GET("offers/{offerId}/is-favourited")
    Call<Boolean> isOfferFavourited(@Path("offerId") int offerId);

    @POST("providers/")
    Call<Offer> createProviderService(@Body NewOfferDTO offer);

    @DELETE("providers/{offerId}")
    Call<Void> deleteProviderService(@Path("offerId") int id);

    @GET("offers/search-services")
    Call<PagedResponse<Offer>> getSearchedService( @Query("name") String name,  @Query("page") int page, @Query("pageSize") int size);
    @GET("offers/search-services")
    Call<PagedResponse<Offer>> getFilteredServices(
            @Query("category") String category,
            @Query("eventType") String eventType,
            @Query("isOnSale") Boolean isOnSale,
            @Query("isAvailable") Boolean isAvailable,
            @Query("maxPrice") Double maxPrice,
            @Query("page") int page,
            @Query("pageSize") int size
    );

    @GET(ClientUtils.FILTERED_OFFERS)
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: true"
    })
    Call<PagedResponse<OfferDTO>> getFilteredOffers(
            @Query("name") String name,
            @Query("description") String description,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("isOnSale") Boolean isOnSale,
            @Query("category") String category,
            @Query("eventType") String eventType,
            @Query("isService") Boolean isService,
            @Query("isProduct") Boolean isProduct,
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("sortDir") String sortDir
    );

    @GET("offers/{offerId}/purchased")
    Call<Boolean> isOfferPurchased(
            @Path("offerId") int offerId
    );

    @GET("offers/{offerId}/provider")
    Call<UserDTO> getProvider(@Path("offerId") int offerId);

    @GET("offers/{offerId}/rating")
    Call<Double> getRating(@Path("offerId") int offerId);

    @POST("products")
    Call<Offer> createProviderProduct(@Body NewOfferDTO newOffer);

    @PUT("products/{productId}")
    Call<Offer> editProviderProduct(@Path("productId") int offerId, @Body NewOfferDTO dto);

    @DELETE("products/{productId}")
    Call<Void> deleteProviderProduct(@Path("productId") int id);

    @GET("providers/my-products")
    Call<PagedResponse<Offer>> getMyProducts(@Query("page") int page, @Query("size") int size);

}

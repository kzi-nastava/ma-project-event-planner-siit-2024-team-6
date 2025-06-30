package com.example.eventure.clients;

import com.example.eventure.dto.CategoryDTO;
import com.example.eventure.dto.NewCategoryDTO;
import com.example.eventure.model.Category;
import com.example.eventure.model.CategorySuggestion;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CategoryService {
    @GET("providers/categories")
    Call<List<String>> getAllCategoryNames();
    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
            "skip: false"
    })
    @GET("offers/categories")
    Call<List<String>> getCategories();

    @GET(ClientUtils.CATEGORIES)
    Call<PagedResponse<Category>> getPagedCategories(
            @Query("page") int page,
            @Query("size") int size
    );
    @POST(ClientUtils.CREATE_CATEGORY)
    Call<Category> createCategory(@Body NewCategoryDTO dto);

    @PUT(ClientUtils.DELETE_UPDATE_CATEGORY)
    Call<Category> updateCategory(@Path("id") int id, @Body NewCategoryDTO dto);

    @DELETE(ClientUtils.DELETE_UPDATE_CATEGORY)
    Call<ResponseBody> deleteCategory(@Path("id") int id);

    @GET(ClientUtils.CATEGORY_SUGGESTIONS)
    Call<PagedResponse<CategorySuggestion>> getCategorySuggestions(@Query("page") int page, @Query("size") int size);

    @PUT(ClientUtils.APPROVE_CATEGORY_SUGGETION)
    Call<CategorySuggestion> approveSuggestion(@Path("id") int id);

    @PUT(ClientUtils.UPDATE_CATEGORY_SUGGESTION)
    Call<CategorySuggestion> updateSuggestion(@Path("id") int id, @Body NewCategoryDTO dto);

    @PUT(ClientUtils.REJECT_CATEGORY_SUGGETION)
    Call<CategorySuggestion> rejectSuggestion(@Path("id") int id, @Query("categoryName") String categoryName);
    @GET("organizers/category-names")
    Call<List<String>> getCategoryNames();

    @GET()
    Call<List<CategoryDTO>> getAllCategories();
}

package com.example.eventure.clients;

import com.example.eventure.model.Category;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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

    @GET("admins/categories")
    Call<PagedResponse<Category>> getPagedCategories(
            @Query("page") int page,
            @Query("size") int size
    );
}

package com.example.eventure.clients;

import com.example.eventure.model.Category;
import com.example.eventure.model.Offer;
import com.example.eventure.model.PagedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CategoryService {
    @GET("providers/categories")
    Call<List<String>> getAllCategoryNames();

}

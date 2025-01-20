package com.example.eventure.clients;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.eventure.BuildConfig;

public class ClientUtils {
    public static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/"; // For Android emulator
    public static final String LOGIN = "users/login";
    public static final String TOP_FIVE_EVENTS = "events/top-five";
    public static final String ALL_EVENTS = "events/";
    public static final String ALL_EVENTS_PAGED = "events/all-elements";
    public static final String TOP_FIVE_OFFERS = "offers/top-five";
    public static final String ALL_OFFERS = "offers/";
    public static final String ALL_OFFERS_PAGED = "offers/all-elements";

    // Create a custom Gson instance with LocalDateTime deserializer
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString()))
            .create();

    public static OkHttpClient test() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(test())
            .build();

    public static final EventTypeService eventTypeService = retrofit.create(EventTypeService.class);
    public static final EventService eventService = retrofit.create(EventService.class);
    public static final OfferService offerService = retrofit.create(OfferService.class);
    public static final CategoryService categoryService = retrofit.create(CategoryService.class);
}

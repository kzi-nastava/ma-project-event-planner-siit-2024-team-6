package com.example.eventure.clients;

import android.content.Context;
import android.util.Log;

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
    public static final String FILTERED_EVENTS = "events/search";

    public static final String TOP_FIVE_OFFERS = "offers/top-five";
    public static final String ALL_OFFERS = "offers/";
    public static final String ALL_OFFERS_PAGED = "offers/all-elements";
    public static final String FILTERED_OFFERS = "offers/search";
    public static final String ALL_EVENT_TYPES = "admins/event-types";
    public static final int PAGE_SIZE = 5;

    private static AuthService authService;
    public static void initializeAuthService(Context context) {
        authService = new AuthService(context.getApplicationContext());
    }

    public static AuthService getAuthService() {
        if (authService == null) {
            throw new IllegalStateException("AuthService not initialized. Call initializeAuthService() first.");
        }
        return authService;
    }
    // Create a custom Gson instance with LocalDateTime deserializer
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString()))
            .create();

    public static OkHttpClient test() {
        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    okhttp3.Request originalRequest = chain.request();
                    String skipHeader = originalRequest.header("skip");
                    if(authService.isLoggedIn()){
                        if(authService.hasTokenExpired()){
                            Log.d("AuthTag", "TOKEN EXPIRED SO USER LOGGED OUT: " + originalRequest.url());
                            authService.logout();
                        }
                    }

                    if ("true".equals(skipHeader)) {
                        Log.d("AuthTag", "Skipping token for request: " + originalRequest.url());
                        okhttp3.Request newRequest = originalRequest.newBuilder()
                                .removeHeader("skip")
                                .build();
                        return chain.proceed(newRequest);
                    }

                    String token = authService.getToken();
                    if (authService.isLoggedIn()) {
                        Log.d("AuthTag", "Adding token to request: " + originalRequest.url());
                        okhttp3.Request newRequest = originalRequest.newBuilder()
                                .addHeader("X-Auth-Token", "Bearer " + token)
                                .build();
                        return chain.proceed(newRequest);
                    }
                    Log.d("AuthTag", "No token available or user is not logged in");
                    return chain.proceed(originalRequest);
                })
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
    public static final LoginService loginService = retrofit.create(LoginService.class);
    //public static final AuthService authService = retrofit.create(AuthService.class);
}

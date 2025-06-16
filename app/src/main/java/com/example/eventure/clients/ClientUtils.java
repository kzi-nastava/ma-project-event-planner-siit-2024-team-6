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
import org.json.JSONObject;
import org.json.JSONException;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


public class ClientUtils {
    public static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/"; // For Android emulator
    //Events
    public static final String LOGIN = "users/login";
    public static final String TOP_FIVE_EVENTS = "events/top-five";
    public static final String ALL_EVENTS = "events/";
    public static final String ALL_EVENTS_PAGED = "events/all-elements";
    public static final String FILTERED_EVENTS = "events/search";

    //Offers
    public static final String TOP_FIVE_OFFERS = "offers/top-five";
    public static final String ALL_OFFERS = "offers/";

    public static final String PRICE_LIST = "providers/price-list";
    public static final String UPDATE_PRICE_LIST = "providers/price/{id}";
    public static final String ALL_OFFERS_PAGED = "offers/all-elements";
    public static final String ACCEPTED_OFFERS = "offers/accepted";
    public static final String FILTERED_OFFERS = "offers/search";
    public static final String ALL_EVENT_TYPES = "admins/event-types";
    // Organizer
    public static final String ORGANIZER_FUTURE_EVENTS = "organizers/future-events";
    public static final String ADD_REACTION = "reactions/";
    public static final String REACTIONS = "reactions/";
    public static final String PENDING_REACTIONS = "reactions/pending";
    public static final String ADD_RESERVATION = "reservations/";

    //Reports
    public static final String REPORTS = "report";
    public static final String APPROVE_REPORT = "report/{id}/approve";
    public static final String REJECT_REPORT = "report/{id}";

    //Notifications
    public static final String NOTIFICATIONS = "notifications/";
    public static final String RECEIVER_NOTIFICATIONS = "notifications/receiver/{receiverId}";
    public static final String MUTE = "users/mute/{userId}";



    public static final String CATEGORIES = "admins/categories";
    public static final String DELETE_UPDATE_CATEGORY = "admins/category/{id}";
    public static final String CREATE_CATEGORY = "admins/category";
    public static final String APPROVE_CATEGORY_SUGGETION = "admins/suggestion/approve/{id}";
    public static final String REJECT_CATEGORY_SUGGETION = "admins/suggestion/reject/{id}";
    public static final String CATEGORY_SUGGESTIONS = "admins/suggestions";
    public static final String UPDATE_CATEGORY_SUGGESTION = "admins/suggestion/{id}";

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
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                // interceptor for adding token to requests
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
                // interceptor checking if response is token expired
                .addInterceptor(chain -> {
                    okhttp3.Request request = chain.request();
                    okhttp3.Response response = chain.proceed(request);

                    if (response.code() == 401) {
                        okhttp3.ResponseBody responseBody = response.peekBody(Long.MAX_VALUE);
                        String responseBodyString = responseBody.string();

                        try {
                            JSONObject jsonObject = new JSONObject(responseBodyString);
                            if (jsonObject.has("error")) {
                                String errorMessage = jsonObject.getString("error");
                                if ("Token expired".equals(errorMessage)) {
                                    // Token expired, logout and navigate to login
                                    authService.logout();
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Toast.makeText(authService.getContext(),
                                                "Session expired. Please login again to access all features.",
                                                Toast.LENGTH_LONG).show();
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("AuthInterceptor", "Failed to parse error response JSON", e);
                        }
                    }

                    return response;
                })

                .addInterceptor(chain -> {
                    // Get the outgoing request
                    okhttp3.Request request = chain.request();

                    // Check and log if the Authorization header is present
                    String authorizationHeader = request.header("X-Auth-Token");
                    if (authorizationHeader != null) {
                        Log.d("AuthTag", "Authorization Token attached: " + authorizationHeader);
                    } else {
                        Log.d("AuthTag", "No Authorization token attached to the request");
                    }

                    // Proceed with the request
                    return chain.proceed(request);
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
    public static final OrganizerService organizerService = retrofit.create(OrganizerService.class);
    public static final ReservationService reservationService = retrofit.create(ReservationService.class);
    public static final LoginService loginService = retrofit.create(LoginService.class);

    public static final ReactionService reactionService = retrofit.create(ReactionService.class);
    public static final ReportService reportService = retrofit.create(ReportService.class);
    public static final NotificationService notificationService = retrofit.create(NotificationService.class);

    public static final ChatService chatService = retrofit.create(ChatService.class);
    //public static final AuthService authService = retrofit.create(AuthService.class);
}

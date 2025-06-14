package com.example.eventure.clients;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.example.eventure.R;
import com.example.eventure.activities.LoginActivity;
import com.example.eventure.fragments.LoginFragment;

public class AuthService {

    private static final String SHARED_PREFS = "auth_prefs";
    private static final String TOKEN_KEY = "user";
    private static final String MUTED_KEY = "muted";
    private final SharedPreferences sharedPreferences;
    private Context context;

    public AuthService(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }
    public Context getContext(){
        return this.context;
    }

    /**
     * Save the JWT token to shared preferences.
     */
    public void login(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }
    public void saveMuted(boolean muted) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MUTED_KEY, muted);
        editor.apply();
    }

    public boolean isMuted() {
        return sharedPreferences.getBoolean(MUTED_KEY, false);
    }

    public String getRole() {
        String token = sharedPreferences.getString(TOKEN_KEY, null);
        if (token != null && isLoggedIn()) {
            try {
                JWT jwt = new JWT(token);
                return jwt.getClaim("role").asString();
            } catch (Exception e) {
                Log.e("AuthService", "Error decoding token", e);
            }
        }
        return null;
    }
    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    /**
     * Get the user's ID from the JWT token.
     */
    public Integer getUserId() {
        String token = sharedPreferences.getString(TOKEN_KEY, null);
        if (token != null && isLoggedIn()) {
            try {
                JWT jwt = new JWT(token);
                return jwt.getClaim("userId").asInt();
            } catch (Exception e) {
                Log.e("AuthTag", "Error decoding token", e);
            }
        }
        return null;
    }

    public boolean isLoggedIn() {
        String token = sharedPreferences.getString(TOKEN_KEY, null);
        if (token != null) {
            return true;
        }
        return false;
    }
    public boolean hasTokenExpired() {
        String token = sharedPreferences.getString(TOKEN_KEY, null);
        try {
            JWT jwt = new JWT(token);
            return jwt.isExpired(10); // Check expiration with 10 seconds leeway
        } catch (Exception e) {
            Log.e("AuthTag", "Error checking token expiration", e);
        }
        return false;
    }

    /**
     * Logout the user by removing the token from shared preferences.
     */
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.remove(MUTED_KEY);
        editor.apply();

        // Disconnect from WebSocket (notification socket)
        NotificationSocketManager.getInstance().disconnect();

        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment()) // Replace with your container ID and fragment
                    .commit();
        } else {
            Log.e("NavigationError", "Context is not an instance of AppCompatActivity, cannot switch to fragment.");
        }

        Log.d("AuthTag", "User logged out.");
    }
}


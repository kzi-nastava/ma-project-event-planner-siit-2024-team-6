package com.example.eventure.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.android.jwt.JWT;

public class AuthService {

    private static final String SHARED_PREFS = "auth_prefs";
    private static final String TOKEN_KEY = "user";
    private final SharedPreferences sharedPreferences;

    public AuthService(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    /**
     * Save the JWT token to shared preferences.
     */
    public void login(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
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
            try {
                JWT jwt = new JWT(token);
                return !jwt.isExpired(10); // Check expiration with 10 seconds leeway
            } catch (Exception e) {
                Log.e("AuthService", "Error checking token expiration", e);
            }
        }
        return false;
    }

    /**
     * Logout the user by removing the token from shared preferences.
     */
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
        Log.d("AuthTag", "User logged out.");
    }
}


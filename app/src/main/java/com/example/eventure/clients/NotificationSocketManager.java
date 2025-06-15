package com.example.eventure.clients;

import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.CLOSED;
import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.ERROR;
import static ua.naiksoftware.stomp.dto.LifecycleEvent.Type.OPENED;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.eventure.dto.NotificationDTO;
import com.google.gson.Gson;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class NotificationSocketManager {

    private static NotificationSocketManager instance;
    private StompClient stompClient;
    private boolean connected = false;
    private static final String TAG = "NotificationSocket";
    private AuthService authService;

    private NotificationSocketManager() {}

    public static synchronized NotificationSocketManager getInstance() {
        if (instance == null) {
            instance = new NotificationSocketManager();
        }
        return instance;
    }

    public void connect(Context context, int userId) {
        if (connected) return;
        authService = new AuthService(context);

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/socket");

        stompClient.lifecycle().subscribe(event -> {
            switch (event.getType()) {
                case OPENED:
                    Log.d(TAG, "Socket connected");
                    Log.d("AuthTag","Socket connected");

                    connected = true;

                    stompClient.topic("/socket-publisher/notifications/" + userId)
                            .subscribe(message -> {
                                String json = message.getPayload();
                                Log.d(TAG, "Received: " + json);
                                NotificationDTO notification = new Gson().fromJson(json, NotificationDTO.class);

                                if (!authService.isMuted()) {
                                    // Show Android system notification only if not muted
                                    NotificationHelper.showNotification(context, notification);
                                } else {
                                    Log.d(TAG, "Notifications muted - skipping system notification display");
                                }
                                // Optional: Notify active fragment
                                if (notificationListener != null) {
                                    notificationListener.onNotificationReceived(notification);
                                }
                            },
                                    throwable -> {
                                        Log.e(TAG, "Error in topic subscription", throwable);
                                        // Optionally notify user or reconnect
                                    });
                    break;

                case ERROR:
                    Log.e(TAG, "Socket error", event.getException());
                    break;

                case CLOSED:
                    Log.d(TAG, "Socket closed");
                    connected = false;
                    break;
            }
        });

        stompClient.connect();
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
            connected = false;
            Log.d("AuthTag","Disconnected from socket");
        }
    }

    // Optional listener for live updates
    public interface NotificationListener {
        void onNotificationReceived(NotificationDTO notification);
    }

    private NotificationListener notificationListener;

    public void setNotificationListener(NotificationListener listener) {
        this.notificationListener = listener;
    }

    public void removeNotificationListener() {
        this.notificationListener = null;
    }
}

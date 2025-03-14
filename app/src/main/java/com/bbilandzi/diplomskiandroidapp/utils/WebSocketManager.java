package com.bbilandzi.diplomskiandroidapp.utils;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.CLIENT_ID;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bbilandzi.diplomskiandroidapp.BuildConfig;
import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.WebsocketMessageDTO;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static final String CHANNEL_ID = "WebSocketChannel";
    private static final String SERVER_URL = BuildConfig.WS_URL;
    private final Map<MessageTypes, MutableLiveData<WebsocketMessageDTO>> messageTypeLiveDataMap = new HashMap<>();

    private Context context;
    private final OkHttpClient client;
    private WebSocket webSocket;
    private final Gson gson;

    public WebSocketManager() {
        client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    public void start(Context context) {
        this.context = context;
        Request request = new Request.Builder().url(SERVER_URL).build();
        createNotificationChannel();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d(TAG, "WebSocket opened: " + response);
                WebsocketMessageDTO message = new WebsocketMessageDTO();
                message.setType(CLIENT_ID);
                message.setPayload(AuthUtils.getUserId(context));
                sendMessage(message);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "Message received: " + text);
                WebsocketMessageDTO message = new Gson().fromJson(text, WebsocketMessageDTO.class);
                MutableLiveData<WebsocketMessageDTO> liveData = messageTypeLiveDataMap.get(message.getType());
                if (liveData != null) {
                    liveData.postValue(message);
                }
                showNotification("test", message.getPayload().toString());
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d(TAG, "WebSocket closing: " + reason);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.e(TAG, "WebSocket error: " + t.getMessage());
            }
        });
    }

    public void stop() {
        if (webSocket != null) {
            webSocket.close(1000, "Client closing");
        }
    }

    public void sendMessage(WebsocketMessageDTO message) {
        if (webSocket != null) {
            String jsonMessage = gson.toJson(message);
            webSocket.send(jsonMessage);
        }
    }

    private void showNotification(String title, String message) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_icon) // Add your own icon
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, notification);
    }

    private void createNotificationChannel() {
        CharSequence name = "test";
        String description = "desc";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(context, NotificationManager.class);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    public LiveData<WebsocketMessageDTO> getMessageLiveData(MessageTypes messageType) {
        return messageTypeLiveDataMap.computeIfAbsent(messageType, k -> new MutableLiveData<>());
    }
}

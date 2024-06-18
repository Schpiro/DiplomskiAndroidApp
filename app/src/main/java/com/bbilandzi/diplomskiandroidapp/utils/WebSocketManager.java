package com.bbilandzi.diplomskiandroidapp.utils;

import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.CLIENT_ID;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
    private static final String SERVER_URL = "ws://192.168.1.64:8081/socket/test";
    private static WebSocketManager instance;
    private final Map<MessageTypes, MutableLiveData<WebsocketMessageDTO>> messageTypeLiveDataMap = new HashMap<>();



    private OkHttpClient client;
    private WebSocket webSocket;
    private Gson gson;

    public MutableLiveData<WebsocketMessageDTO> messageLiveData = new MutableLiveData<>();

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public WebSocketManager() {
        client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    public void start(Context context) {
        Request request = new Request.Builder().url(SERVER_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket opened: " + response);
                WebsocketMessageDTO message = new WebsocketMessageDTO();
                message.setType(CLIENT_ID);
                message.setPayload(AuthUtils.getUserId(context));
                sendMessage(message);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Message received: " + text);
                WebsocketMessageDTO message = new Gson().fromJson(text, WebsocketMessageDTO.class);
                MutableLiveData<WebsocketMessageDTO> liveData = messageTypeLiveDataMap.get(message.getType());
                if (liveData != null) {
                    liveData.postValue(message);
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closing: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
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

    public LiveData<WebsocketMessageDTO> getMessageLiveData(MessageTypes messageType) {
        return messageTypeLiveDataMap.computeIfAbsent(messageType, k -> new MutableLiveData<>());
    }

    private void handleWebSocketMessage(WebsocketMessageDTO message) {
        switch (message.getType()) {
            case CLIENT_ID:
                // Handle CLIENT_ID
                break;
            case PRIVATE_MESSAGE:
                // Handle PRIVATE_MESSAGE
                break;
            case GROUP_MESSAGE:
                // Handle GROUP_MESSAGE
                break;
            case NEW_GROUP:
                // Handle NEW_GROUP
                break;
            case NEW_EVENT:
                // Handle NEW_EVENT
                break;
            case NEW_COMMENT:
                // Handle NEW_COMMENT
                break;
            default:
                Log.w(TAG, "Unknown message type: " + message.getType());
                break;
        }
    }
}

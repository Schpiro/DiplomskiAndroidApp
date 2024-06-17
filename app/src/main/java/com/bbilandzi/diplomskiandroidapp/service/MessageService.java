package com.bbilandzi.diplomskiandroidapp.service;

import com.bbilandzi.diplomskiandroidapp.model.MessageDTO;
import com.bbilandzi.diplomskiandroidapp.model.MessageSend;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessageService {
    @GET("message")
    Call<List<MessageDTO>> getAllMessages();

    @GET("message/{id}")
    Call<List<MessageDTO>> getConversationWithUser(@Path("id") Long senderId);

    @GET("message/group/{id}")
    Call<List<MessageDTO>> getConversationWithGroup(@Path("id") Long groupId);

    @POST("message")
    Call<MessageDTO> sendMessage(@Body MessageSend messageSend);
}

package com.bbilandzi.diplomskiandroidapp.service;

import com.bbilandzi.diplomskiandroidapp.model.CommentDTO;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;
import com.bbilandzi.diplomskiandroidapp.model.EventSend;
import com.bbilandzi.diplomskiandroidapp.model.MessageDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventService {
    @GET("event")
    Call<List<EventDTO>> getAllEvents();

    @POST("event")
    Call<EventDTO> createEvent(@Body EventSend eventSend);

    @GET("event/comments/{id}")
    Call<List<CommentDTO>> getCommentsForEvent(@Path("id") Long eventId);

    @POST("event/comments/{id}")
    Call<CommentDTO> createCommentForEvent(@Path("id") Long eventId, @Body CommentDTO commentDTO);
}

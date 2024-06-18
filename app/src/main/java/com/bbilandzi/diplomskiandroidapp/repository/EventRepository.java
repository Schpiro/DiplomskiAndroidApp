package com.bbilandzi.diplomskiandroidapp.repository;

import com.bbilandzi.diplomskiandroidapp.model.CommentDTO;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;
import com.bbilandzi.diplomskiandroidapp.model.EventSend;
import com.bbilandzi.diplomskiandroidapp.service.EventService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class EventRepository {
    EventService eventService;

    @Inject
    public EventRepository(Retrofit client) {
        this.eventService = client.create(EventService.class);
    }

    public Call<List<EventDTO>> getAllEvents() {
        return eventService.getAllEvents();
    }

    public Call<EventDTO> createEvent(EventSend eventSend) {
        return eventService.createEvent(eventSend);
    }

    public Call<List<CommentDTO>> getCommentsForEvent(Long eventId) {
       return eventService.getCommentsForEvent(eventId);
    }

    public Call<CommentDTO> createCommentForEvent(Long eventId, CommentDTO commentDTO) {
        return eventService.createCommentForEvent(eventId, commentDTO);
    }
}

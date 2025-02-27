package com.bbilandzi.diplomskiandroidapp.repository;

import com.bbilandzi.diplomskiandroidapp.model.CommentDTO;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;
import com.bbilandzi.diplomskiandroidapp.service.EventService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Retrofit;

public class EventRepository {
    EventService eventService;

    @Inject
    public EventRepository(Retrofit client) {
        this.eventService = client.create(EventService.class);
    }

    public Call<List<EventDTO>> getAllEvents() {
        return eventService.getAllEvents();
    }

    public Call<EventDTO> createEvent(EventDTO eventDTO) {
        return eventService.createEvent(eventDTO);
    }

    public Call<List<CommentDTO>> getCommentsForEvent(Long eventId) {
       return eventService.getCommentsForEvent(eventId);
    }

    public Call<CommentDTO> createCommentForEvent(Long eventId, CommentDTO commentDTO) {
        return eventService.createCommentForEvent(eventId, commentDTO);
    }
}

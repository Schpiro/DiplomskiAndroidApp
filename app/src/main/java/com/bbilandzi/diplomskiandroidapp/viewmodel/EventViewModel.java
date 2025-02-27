package com.bbilandzi.diplomskiandroidapp.viewmodel;

import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.NEW_COMMENT;
import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.NEW_EVENT;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbilandzi.diplomskiandroidapp.model.CommentDTO;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;
import com.bbilandzi.diplomskiandroidapp.model.WebsocketMessageDTO;
import com.bbilandzi.diplomskiandroidapp.repository.EventRepository;
import com.bbilandzi.diplomskiandroidapp.utils.DateTimeUtil;
import com.bbilandzi.diplomskiandroidapp.utils.WebSocketManager;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class EventViewModel extends ViewModel {
    private final WebSocketManager webSocketManager;
    private final EventRepository eventRepository;
    private final MutableLiveData<List<EventDTO>> eventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<CommentDTO>> commentsLiveData = new MutableLiveData<>();
    private final Gson gson = new Gson();
    private List<EventDTO> originalEvents = new ArrayList<>();
    @Setter
    private boolean oldestFirstFilter = false;

    @Inject
    public EventViewModel(EventRepository eventRepository, WebSocketManager webSocketManager) {
        this.eventRepository = eventRepository;
        this.webSocketManager = webSocketManager;
        webSocketManager.getMessageLiveData(NEW_COMMENT).observeForever(this::onNewCommentReceived);
        webSocketManager.getMessageLiveData(NEW_EVENT).observeForever(this::onNewEventReceived);
    }

    public LiveData<List<EventDTO>> getEventsLiveData() {
        return eventsLiveData;
    }

    public LiveData<List<CommentDTO>> getCommentsLiveData() {
        return commentsLiveData;
    }

    public void getAllEvents() {
        eventRepository.getAllEvents().enqueue(new Callback<List<EventDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<EventDTO>> call, @NonNull Response<List<EventDTO>> response) {
                if (response.isSuccessful()) {
                    List<EventDTO> events = response.body();
                    assert events != null;
                    events.sort(DateTimeUtil.eventDateComparator());
                    eventsLiveData.setValue(events);
                    originalEvents.clear();
                    originalEvents.addAll(events);
                } else {
                    Log.e("EventViewModel", "getAllEvents: Failed with code " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<EventDTO>> call, @NonNull Throwable throwable) {
                Log.e("EventViewModel", "getAllEvents: Failed", throwable);
            }
        });
    }

    public void filterEvents(Long date) {
        List<EventDTO> currentEvents = new ArrayList<>(originalEvents);

        List<EventDTO> filteredEvents = new ArrayList<>();

        for (EventDTO event : currentEvents) {
            LocalDateTime localDateTime = LocalDateTime.parse(event.getDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            long eventEpochMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            if (date == null || eventEpochMillis >= date) {
                filteredEvents.add(event);
            }
        }

        eventsLiveData.setValue(filteredEvents);
    }

    public void removeAllFilters() {
        eventsLiveData.setValue(originalEvents);
    }

    public void getCommentsForEvent(Long eventId) {
        eventRepository.getCommentsForEvent(eventId).enqueue(new Callback<List<CommentDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<CommentDTO>> call, @NonNull Response<List<CommentDTO>> response) {
                if (response.isSuccessful()) {
                    List<CommentDTO> comments = response.body();
                    assert comments != null;
                    comments.sort(oldestFirstFilter ? DateTimeUtil.commentDateComparatorReverse() : DateTimeUtil.commentDateComparator());
                    commentsLiveData.setValue(comments);
                    Log.d("EventViewModel", "getCommentsForEvent: " + comments);
                } else {
                    Log.e("EventViewModel", "getCommentsForEvent: Failed with code " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CommentDTO>> call, @NonNull Throwable throwable) {
                Log.e("EventViewModel", "getCommentsForEvent: Failed", throwable);
            }
        });
    }

    public void createEvent(EventDTO eventDTO) {
        eventRepository.createEvent(eventDTO).enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(@NonNull Call<EventDTO> call, @NonNull Response<EventDTO> response) {
                if (response.isSuccessful()) {
                    EventDTO eventDTO = response.body();
                    Log.d("EventViewModel", "createEvent: " + eventDTO);
                    WebsocketMessageDTO message = new WebsocketMessageDTO();
                    message.setType(NEW_EVENT);
                    message.setPayload(gson.toJson(eventDTO));
                    webSocketManager.sendMessage(message);
                } else {
                    Log.e("EventViewModel", "createEvent: Failed with code " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventDTO> call, @NonNull Throwable throwable) {
                Log.e("EventViewModel", "createEvent: Failed", throwable);
            }
        });
    }

    public void createCommentForEvent(Long eventId, CommentDTO commentDTO) {
        eventRepository.createCommentForEvent(eventId, commentDTO).enqueue(new Callback<CommentDTO>() {
            @Override
            public void onResponse(@NonNull Call<CommentDTO> call, @NonNull Response<CommentDTO> response) {
                if (response.isSuccessful()) {
                    CommentDTO createdComment = response.body();
                    Log.d("EventViewModel", "createCommentForEvent: " + createdComment);
                    WebsocketMessageDTO message = new WebsocketMessageDTO();
                    message.setType(NEW_COMMENT);
                    message.setPayload(gson.toJson(commentDTO));
                    webSocketManager.sendMessage(message);
                } else {
                    Log.e("EventViewModel", "createCommentForEvent: Failed with code " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommentDTO> call, @NonNull Throwable throwable) {
                Log.e("EventViewModel", "createCommentForEvent: Failed", throwable);
            }
        });
    }

    private void onNewCommentReceived(WebsocketMessageDTO message) {
        CommentDTO newComment = gson.fromJson(gson.toJson(message.getPayload()), CommentDTO.class);
        List<CommentDTO> currentList = commentsLiveData.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        newComment.setCreationDate(DateTimeUtil.systemTime(newComment.getCreationDate()));
        currentList.add(newComment);
        currentList.sort(oldestFirstFilter ? DateTimeUtil.commentDateComparatorReverse() : DateTimeUtil.commentDateComparator());
        commentsLiveData.postValue(currentList);
    }

    private void onNewEventReceived(WebsocketMessageDTO message) {
        EventDTO newEvent = gson.fromJson(gson.toJson(message.getPayload()), EventDTO.class);
        List<EventDTO> currentList = eventsLiveData.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(newEvent);
        currentList.sort(DateTimeUtil.eventDateComparator());
        eventsLiveData.postValue(currentList);
        originalEvents = currentList;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        webSocketManager.getMessageLiveData(NEW_COMMENT).removeObserver(this::onNewCommentReceived);
    }
}

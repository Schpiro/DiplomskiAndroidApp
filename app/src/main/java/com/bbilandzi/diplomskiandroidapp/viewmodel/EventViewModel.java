package com.bbilandzi.diplomskiandroidapp.viewmodel;

import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.NEW_COMMENT;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbilandzi.diplomskiandroidapp.model.CommentDTO;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;
import com.bbilandzi.diplomskiandroidapp.model.EventSend;
import com.bbilandzi.diplomskiandroidapp.model.WebsocketMessageDTO;
import com.bbilandzi.diplomskiandroidapp.repository.EventRepository;
import com.bbilandzi.diplomskiandroidapp.utils.WebSocketManager;
import com.google.gson.Gson;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
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

    @Inject
    public EventViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.webSocketManager = WebSocketManager.getInstance();
        webSocketManager.getMessageLiveData(NEW_COMMENT).observeForever(this::onNewCommentReceived);
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
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                if (response.isSuccessful()) {
                    eventsLiveData.setValue(response.body());
                } else {
                    Log.e("EventViewModel", "getAllEvents: Failed with code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable throwable) {
                Log.e("EventViewModel", "getAllEvents: Failed", throwable);
            }
        });
    }

    public void getCommentsForEvent(Long eventId) {
        eventRepository.getCommentsForEvent(eventId).enqueue(new Callback<List<CommentDTO>>() {
            @Override
            public void onResponse(Call<List<CommentDTO>> call, Response<List<CommentDTO>> response) {
                if (response.isSuccessful()) {
                    List<CommentDTO> comments = response.body();
                    commentsLiveData.setValue(comments);
                    Log.d("EventViewModel", "getCommentsForEvent: " + comments);
                    // Handle the comments list as needed
                } else {
                    Log.e("EventViewModel", "getCommentsForEvent: Failed with code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CommentDTO>> call, Throwable throwable) {
                Log.e("EventViewModel", "getCommentsForEvent: Failed", throwable);
            }
        });
    }

    public void createEvent(EventSend eventSend) {
        eventRepository.createEvent(eventSend).enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.isSuccessful()) {
                    EventDTO eventDTO = response.body();
                    Log.d("EventViewModel", "createEvent: " + eventDTO);
                    // Handle the created event object as needed
                } else {
                    Log.e("EventViewModel", "createEvent: Failed with code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable throwable) {
                Log.e("EventViewModel", "createEvent: Failed", throwable);
            }
        });
    }

    public void createCommentForEvent(Long eventId, CommentDTO commentDTO) {
        eventRepository.createCommentForEvent(eventId, commentDTO).enqueue(new Callback<CommentDTO>() {
            @Override
            public void onResponse(Call<CommentDTO> call, Response<CommentDTO> response) {
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
            public void onFailure(Call<CommentDTO> call, Throwable throwable) {
                Log.e("EventViewModel", "createCommentForEvent: Failed", throwable);
            }
        });
    }

    private void onNewCommentReceived(WebsocketMessageDTO message) {
        CommentDTO newComment = gson.fromJson((String) message.getPayload(), CommentDTO.class);
        newComment.setCreationDate(String.valueOf(Instant.now().toEpochMilli()));
        List<CommentDTO> currentList = commentsLiveData.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(newComment);
        commentsLiveData.postValue(currentList);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        webSocketManager.getMessageLiveData(NEW_COMMENT).removeObserver(this::onNewCommentReceived);
    }
}

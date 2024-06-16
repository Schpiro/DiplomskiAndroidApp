package com.bbilandzi.diplomskiandroidapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbilandzi.diplomskiandroidapp.model.MessageDTO;
import com.bbilandzi.diplomskiandroidapp.model.MessageSend;
import com.bbilandzi.diplomskiandroidapp.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class MessageViewModel extends ViewModel {
    private final MessageRepository messageRepository;

    private final MutableLiveData<List<MessageDTO>> fetchedMessages = new MutableLiveData<>();

    @Inject
    public MessageViewModel(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public LiveData<List<MessageDTO>> getFetchedMessages() {
        return fetchedMessages;
    }

    public void getConversationWithUser(Long userId) {
        Call<List<MessageDTO>> call = messageRepository.getConversationWithUser(userId);

        call.enqueue(new Callback<List<MessageDTO>>() {
            @Override
            public void onResponse(Call<List<MessageDTO>> call, Response<List<MessageDTO>> response) {
                if (response.isSuccessful()) {
                    List<MessageDTO> message = response.body();
                    fetchedMessages.postValue(message);
                    Log.d("Messages", message.toString());
                }
            }

            @Override
            public void onFailure(Call<List<MessageDTO>> call, Throwable throwable) {
                Log.e("GetAllMessages Error", "Failed: " + throwable.getMessage());
            }
        });
    }

    public void sendMessage(MessageSend messageSend) {
        Call<MessageDTO> call = messageRepository.sendMessage(messageSend);

        call.enqueue(new Callback<MessageDTO>() {
            @Override
            public void onResponse(Call<MessageDTO> call, Response<MessageDTO> response) {
                if (response.isSuccessful()) {
                    List<MessageDTO> message = fetchedMessages.getValue();
                    if (message == null) {
                        message = new ArrayList<>();
                    }
                    message.add(response.body());
                    fetchedMessages.postValue(message);
                    Log.d("Message", message.toString());
                }
            }

            @Override
            public void onFailure(Call<MessageDTO> call, Throwable throwable) {
                Log.e("sendMessage Error", "Failed: " + throwable.getMessage());
            }
        });
    }
}

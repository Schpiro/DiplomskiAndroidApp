package com.bbilandzi.diplomskiandroidapp.viewmodel;

import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.GROUP_MESSAGE;
import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.NEW_MESSAGE;
import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.PRIVATE_MESSAGE;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbilandzi.diplomskiandroidapp.model.MessageDTO;
import com.bbilandzi.diplomskiandroidapp.model.MessageSend;
import com.bbilandzi.diplomskiandroidapp.model.WebsocketMessageDTO;
import com.bbilandzi.diplomskiandroidapp.repository.MessageRepository;
import com.bbilandzi.diplomskiandroidapp.utils.WebSocketManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class MessageViewModel extends ViewModel {
    private final WebSocketManager webSocketManager;

    private final MessageRepository messageRepository;

    private final MutableLiveData<List<MessageDTO>> fetchedMessages = new MutableLiveData<>();
    private final Gson gson = new Gson();

    private Long currentRecipientId;
    private boolean currentIsGroupChat;
    @Inject
    public MessageViewModel(MessageRepository messageRepository) {
        this.webSocketManager = WebSocketManager.getInstance();
        this.messageRepository = messageRepository;
        webSocketManager.getMessageLiveData(NEW_MESSAGE).observeForever(this::onNewMessageReceived);
    }

    public void setCurrentChatDetails(Long recipientId, boolean isGroupChat) {
        this.currentRecipientId = recipientId;
        this.currentIsGroupChat = isGroupChat;
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
                Log.e("GetConversationWithUser Error", "Failed: " + throwable.getMessage());
            }
        });
    }

    public void getConversationWithGroup(Long groupId) {
        Call<List<MessageDTO>> call = messageRepository.getConversationWithGroup(groupId);

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
                Log.e("getConversationWithGroup Error", "Failed: " + throwable.getMessage());
            }
        });
    }

    public void sendMessage(MessageSend messageSend, boolean isGroupChat) {
        Call<MessageDTO> call = messageRepository.sendMessage(messageSend);

        call.enqueue(new Callback<MessageDTO>() {
            @Override
            public void onResponse(Call<MessageDTO> call, Response<MessageDTO> response) {
                if (response.isSuccessful()) {
                    MessageDTO messageDTO = response.body();
                    WebsocketMessageDTO message = new WebsocketMessageDTO();
                    message.setType(isGroupChat ? GROUP_MESSAGE : PRIVATE_MESSAGE);
                    message.setPayload(gson.toJson(messageDTO));
                    webSocketManager.sendMessage(message);
                    Log.d("Message", message.toString());
                }
            }

            @Override
            public void onFailure(Call<MessageDTO> call, Throwable throwable) {
                Log.e("sendMessage Error", "Failed: " + throwable.getMessage());
            }
        });
    }

    private void onNewMessageReceived(WebsocketMessageDTO websocketMessageDTO) {
        MessageDTO newMessage = gson.fromJson(gson.toJson(websocketMessageDTO.getPayload()), MessageDTO.class);
        if ((!currentIsGroupChat && newMessage.getRecipientId() != null && newMessage.getRecipientId().equals(currentRecipientId)) ||
            (currentIsGroupChat && newMessage.getRecipientGroupId() != null && newMessage.getRecipientGroupId().equals(currentRecipientId)))
        {
            List<MessageDTO> currentList = fetchedMessages.getValue();
            if (currentList == null) {
                currentList = new ArrayList<>();
            }
            currentList.add(newMessage);
            fetchedMessages.postValue(currentList);
        }
    }
}

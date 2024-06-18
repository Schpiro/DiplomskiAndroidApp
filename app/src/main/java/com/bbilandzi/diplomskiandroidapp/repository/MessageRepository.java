package com.bbilandzi.diplomskiandroidapp.repository;

import com.bbilandzi.diplomskiandroidapp.model.MessageDTO;
import com.bbilandzi.diplomskiandroidapp.model.MessageSend;
import com.bbilandzi.diplomskiandroidapp.service.MessageService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Retrofit;

public class MessageRepository {
    MessageService messageService;

    @Inject
    public MessageRepository(Retrofit client) {
        this.messageService = client.create(MessageService.class);
    }

    public Call<List<MessageDTO>> getAllMessages() {
        return messageService.getAllMessages();
    }

    public Call<List<MessageDTO>> getConversationWithUser(Long senderId) {
        return messageService.getConversationWithUser(senderId);
    }

    public Call<List<MessageDTO>> getConversationWithGroup(Long groupId) {
        return messageService.getConversationWithGroup(groupId);
    }

    public Call<MessageDTO> sendMessage(MessageSend messageSend) {
        return messageService.sendMessage(messageSend);
    }




}

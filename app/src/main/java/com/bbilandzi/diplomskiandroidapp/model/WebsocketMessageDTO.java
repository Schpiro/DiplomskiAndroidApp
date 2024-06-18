package com.bbilandzi.diplomskiandroidapp.model;

import com.bbilandzi.diplomskiandroidapp.utils.MessageTypes;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebsocketMessageDTO implements Serializable {
    private MessageTypes type;
    private Object payload;
    private Long senderId;
    private List<Long> recipientIds;
    private String senderName;
}

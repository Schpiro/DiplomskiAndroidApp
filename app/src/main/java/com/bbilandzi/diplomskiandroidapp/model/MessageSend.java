package com.bbilandzi.diplomskiandroidapp.model;

import lombok.Builder;

@Builder
public class MessageSend {

    private String messageBody;

    private Long parentMessage;

    private Long recipientId;

    private Long recipientGroupId;

    private Long creatorId;
}

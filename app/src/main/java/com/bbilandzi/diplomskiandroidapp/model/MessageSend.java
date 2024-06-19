package com.bbilandzi.diplomskiandroidapp.model;

import lombok.Builder;
import lombok.Setter;

@Builder
public class MessageSend {

    private String messageBody;

    private Long parentMessage;
    @Setter

    private Long recipientId;
    @Setter
    private Long recipientGroupId;

    private Long creatorId;
}

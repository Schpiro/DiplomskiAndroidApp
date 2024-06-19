package com.bbilandzi.diplomskiandroidapp.model;

import java.util.List;

import lombok.Getter;

public class MessageDTO {
    @Getter
    private Long creatorId;
    private String creator;
    private Long recipientId;
    private Long recipientGroupId;
    private List<Long> groupParticipantsIds;
    private String creationDate;
    @Getter
    private String messageBody;

}

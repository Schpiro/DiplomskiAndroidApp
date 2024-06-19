package com.bbilandzi.diplomskiandroidapp.model;

import java.util.List;

import lombok.Getter;

@Getter
public class MessageDTO {
    private Long creatorId;
    private String creator;
    private Long recipientId;
    private Long recipientGroupId;
    private List<Long> groupParticipantsIds;
    private String creationDate;
    private String messageBody;

}

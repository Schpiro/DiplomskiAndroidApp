package com.bbilandzi.diplomskiandroidapp.model;

import lombok.Builder;

@Builder
public class EventSend {
    private Long creatorId;
    private String title;
    private String details;
    private String date;
    private String location;
}

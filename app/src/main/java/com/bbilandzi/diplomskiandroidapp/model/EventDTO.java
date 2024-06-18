package com.bbilandzi.diplomskiandroidapp.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class EventDTO implements Serializable {
    private Long id;
    private String title;
    private String location;
    private String date;
    private String creator;
    private Long creatorId;
    private String details;
    private String creationDate;
    private boolean expanded;
}

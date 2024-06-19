package com.bbilandzi.diplomskiandroidapp.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGroup {
    private Long id;
    private String groupName;
    private List<Long> groupParticipants;
}

package com.bbilandzi.diplomskiandroidapp.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class CommentDTO implements Serializable {
    private Long id;
    private Long eventId;
    private String creator;
    private Long creatorId;
    private String commentBody;
    private String creationDate;
    private Long parentCommentId;
}

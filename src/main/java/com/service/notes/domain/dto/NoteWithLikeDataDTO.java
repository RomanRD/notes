package com.service.notes.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NoteWithLikeDataDTO {

    private String id;
    private String text;
    private String authorId;
    private Date creationDate;
    private Date editionDate;
    private int likeCount;
    private boolean likedByCurrentUser;

}

package com.service.notes.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoteFormDTO {

    private String id;
    @NotBlank
    private String text;

}

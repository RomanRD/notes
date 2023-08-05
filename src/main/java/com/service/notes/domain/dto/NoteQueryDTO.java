package com.service.notes.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
public class NoteQueryDTO {

    private String authorId;
    private Integer skip = 0;
    private Integer limit = Integer.MAX_VALUE;
    private Sort.Direction direction = Sort.Direction.DESC;
    private SortProperty sortProperty = SortProperty.CREATION_DATE;

    public enum SortProperty {
        AMOUNT_LIKES,
        CREATION_DATE
    }

}

package com.service.notes.persistence.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "notes")
public class Note {

    @Id
    private String id;
    private String text;
    @Indexed
    private String authorId;
    private Date creationDate;
    private Date editionDate;
    private List<String> likes;

}

package com.service.notes.persistence.repository;

import com.service.notes.persistence.entity.Note;
import com.service.notes.persistence.repository.custom.CustomNoteRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends MongoRepository<Note, String>, CustomNoteRepository {

}

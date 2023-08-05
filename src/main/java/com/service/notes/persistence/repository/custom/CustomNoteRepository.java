package com.service.notes.persistence.repository.custom;

import com.service.notes.domain.dto.NoteFormDTO;
import com.service.notes.domain.dto.NoteQueryDTO;
import com.service.notes.domain.dto.NoteWithLikeDataDTO;

import java.util.List;

public interface CustomNoteRepository {

    NoteWithLikeDataDTO findNoteWithLikeData(String noteId, String userId);

    List<NoteWithLikeDataDTO> findNotesWithLikeData(NoteQueryDTO noteQuery, String userId);

    void edit(NoteFormDTO note);

    void addLike(String noteId, String userId);

    void deleteLike(String noteId, String userId);

    void deleteNotesAndLikesByAuthorId(String authorId);

}

package com.service.notes.service;

import com.service.notes.domain.dto.NoteFormDTO;
import com.service.notes.domain.dto.NoteQueryDTO;
import com.service.notes.domain.dto.NoteWithLikeDataDTO;

import java.util.List;

public interface NoteService {

    List<NoteWithLikeDataDTO> getAll(String userId);

    NoteWithLikeDataDTO getById(String noteId, String userId);

    List<NoteWithLikeDataDTO> getByQuery(NoteQueryDTO noteQuery, String userId);

    NoteWithLikeDataDTO create(NoteFormDTO noteFormDTO, String authorId);

    void edit(NoteFormDTO noteFormDTO);

    void addLike(String noteId, String userId);

    void deleteLike(String noteId, String userId);

    void delete(String id);

    void deleteByAuthor(String authorId);

}

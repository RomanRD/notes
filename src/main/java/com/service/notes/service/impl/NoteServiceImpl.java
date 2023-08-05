package com.service.notes.service.impl;

import com.service.notes.domain.dto.NoteFormDTO;
import com.service.notes.domain.dto.NoteQueryDTO;
import com.service.notes.domain.dto.NoteWithLikeDataDTO;
import com.service.notes.persistence.entity.Note;
import com.service.notes.persistence.repository.NoteRepository;
import com.service.notes.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import util.Utility;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final ModelMapper mapper;
    private final NoteRepository noteRepository;

    private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

    @Override
    public List<NoteWithLikeDataDTO> getAll(String userId) {
        return noteRepository.findNotesWithLikeData(new NoteQueryDTO(), userId);
    }

    @Override
    public NoteWithLikeDataDTO getById(String noteId, String userId) {
        return noteRepository.findNoteWithLikeData(noteId, userId);
    }

    @Override
    public List<NoteWithLikeDataDTO> getByQuery(NoteQueryDTO noteQuery, String userId) {
        return noteRepository.findNotesWithLikeData(noteQuery, userId);
    }

    @Override
    public NoteWithLikeDataDTO create(NoteFormDTO noteFormDTO, String authorId) {
        final Note note = mapper.map(noteFormDTO, Note.class);
        note.setCreationDate(new Date());
        note.setAuthorId(authorId);
        final Note save = noteRepository.save(note);

        logger.info("Note " + save.getId() + " created by " + Utility.getCurrentUsername());

        return mapper.map(save, NoteWithLikeDataDTO.class);
    }

    @Override
    public void edit(NoteFormDTO noteFormDTO) {
        noteRepository.edit(noteFormDTO);
        logger.info("Note " + noteFormDTO.getId() + " edited by " + Utility.getCurrentUsername());
    }

    @Override
    public void addLike(String noteId, String userId) {
        noteRepository.addLike(noteId, userId);
    }

    @Override
    public void deleteLike(String noteId, String userId) {
        noteRepository.deleteLike(noteId, userId);
    }

    @Override
    public void delete(String id) {
        noteRepository.deleteById(id);
        logger.info("Note " + id + " deleted by " + Utility.getCurrentUsername());
    }

    @Override
    public void deleteByAuthor(String authorId) {
        noteRepository.deleteNotesAndLikesByAuthorId(authorId);
    }
}

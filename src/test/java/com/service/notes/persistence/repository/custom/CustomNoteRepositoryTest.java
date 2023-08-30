package com.service.notes.persistence.repository.custom;

import com.service.notes.domain.dto.NoteFormDTO;
import com.service.notes.domain.dto.NoteWithLikeDataDTO;
import com.service.notes.persistence.document.Note;
import com.service.notes.persistence.repository.NoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CustomNoteRepositoryTest {     //TODO cover other methods with tests

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private ModelMapper modelMapper;

    @AfterEach
    void clearTestData(){
        noteRepository.deleteAll();
    }

    @Test
    void findNoteWithLikeData_WhenUserLikesNote_ExpectLikedByCurrentUserIsTrue(){
        final Note saveNote = createNote();

        final NoteWithLikeDataDTO expectedNote = modelMapper.map(saveNote, NoteWithLikeDataDTO.class);
        expectedNote.setLikeCount(saveNote.getLikes().size());
        expectedNote.setLikedByCurrentUser(true);

        final NoteWithLikeDataDTO actualNote = noteRepository.findNoteWithLikeData(saveNote.getId(), saveNote.getLikes().get(0));

        assertEquals(expectedNote, actualNote);
    }

    @Test
    void findNoteWithLikeData_WhenUserDoesNotLikeNote_ExpectLikedByCurrentUserIsFalse(){
        final String userIdDoesNotLikeNote = "userIdDoesNotLikeNote";

        final Note saveNote = createNote();

        final NoteWithLikeDataDTO expectedNote = modelMapper.map(saveNote, NoteWithLikeDataDTO.class);
        expectedNote.setLikeCount(saveNote.getLikes().size());
        expectedNote.setLikedByCurrentUser(false);

        final NoteWithLikeDataDTO actualNote = noteRepository.findNoteWithLikeData(saveNote.getId(), userIdDoesNotLikeNote);

        assertEquals(expectedNote, actualNote);
    }

    @Test
    void findNoteWithLikeData_WhenUserIsNull_ExpectNoteWithLikeDataDTO_And_LikedByCurrentUserIsFalse(){
        final Note saveNote = createNote();

        final NoteWithLikeDataDTO expectedNote = modelMapper.map(saveNote, NoteWithLikeDataDTO.class);
        expectedNote.setLikeCount(saveNote.getLikes().size());
        expectedNote.setLikedByCurrentUser(false);
        final NoteWithLikeDataDTO actualNote = noteRepository.findNoteWithLikeData(saveNote.getId(), null);

        assertEquals(expectedNote, actualNote);
    }

    @Test
    void findNoteWithLikeData_WhenNoteIdIsNull_And_UserIsNull_ExpectNoteWithLikeDataDTO_And_LikedByCurrentUserIsFalse(){
        final Note saveNote = createNote();

        final NoteWithLikeDataDTO actualNote = noteRepository.findNoteWithLikeData(null, null);

        assertNull(actualNote);
    }

    @Test
    void edit_ExpectUpdateText_And_EditionDate(){
        final Note saveNote = createNote();

        assertNull(saveNote.getEditionDate());

        NoteFormDTO noteFormDTO = new NoteFormDTO();
        noteFormDTO.setId(saveNote.getId());
        noteFormDTO.setText("testText2");
        noteRepository.edit(noteFormDTO);

        final Note byId = noteRepository.findById(saveNote.getId()).get();
        assertEquals(noteFormDTO.getText(), byId.getText());
        assertNotNull(byId.getEditionDate());
    }

    private Note createNote(){
        Note note = new Note();
        note.setText("testText");
        note.setAuthorId("testAuthorId");
        note.setCreationDate(new Date());
        note.setLikes(List.of("authorId1", "authorId2"));

        return noteRepository.save(note);
    }

}

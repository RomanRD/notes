package com.service.notes.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.notes.config.SecurityConfig;
import com.service.notes.domain.dto.NoteFormDTO;
import com.service.notes.domain.dto.NoteWithLikeDataDTO;
import com.service.notes.model.Role;
import com.service.notes.persistence.document.Note;
import com.service.notes.persistence.document.User;
import com.service.notes.security.JwtAuthenticationFilter;
import com.service.notes.service.NoteService;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(NoteController.class)
@ContextConfiguration(classes = NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class NoteControllerTest {     //TODO cover other methods with tests

    static final String CONTROLLER_PATH = "/note";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @Test
    public void edit_WhenUserIsOwner_ExpectOk() throws Exception {
        final NoteFormDTO noteFormDTO = createNoteFormDTO();

        final User currentUser = createUser();
        currentUser.setRoles(List.of(Role.USER));

        final Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(currentUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final NoteWithLikeDataDTO existingNote = new NoteWithLikeDataDTO();
        existingNote.setAuthorId(currentUser.getId());

        when(noteService.getById(noteFormDTO.getId(), null)).thenReturn(existingNote);

        mockMvc.perform(MockMvcRequestBuilders.put(CONTROLLER_PATH + "/edit")
                        .with(user(currentUser))
                        .content(asJsonString(noteFormDTO))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(noteService, times(1)).edit(noteFormDTO);
    }

    @Test
    public void edit_WhenUserIsAdmin_ExpectOk() throws Exception {
        final NoteFormDTO noteFormDTO = createNoteFormDTO();

        final User currentUser = createUser();
        currentUser.setRoles(List.of(Role.ADMIN));

        final Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(currentUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final NoteWithLikeDataDTO existingNote = new NoteWithLikeDataDTO();
        existingNote.setAuthorId("anotherUserId");

        when(noteService.getById(noteFormDTO.getId(), null)).thenReturn(existingNote);

        mockMvc.perform(MockMvcRequestBuilders.put(CONTROLLER_PATH + "/edit")
                        .with(user(currentUser))
                        .content(asJsonString(noteFormDTO))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(noteService, times(1)).edit(noteFormDTO);
    }

    @Test
    public void edit_WhenUserIsNotOwner_ExpectForbidden() throws Exception {
        final NoteFormDTO noteFormDTO = createNoteFormDTO();

        final User currentUser = createUser();
        currentUser.setRoles(List.of(Role.USER));

        final Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(currentUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final NoteWithLikeDataDTO existingNote = new NoteWithLikeDataDTO();
        existingNote.setAuthorId("anotherUserId");

        when(noteService.getById(noteFormDTO.getId(), null)).thenReturn(existingNote);

        mockMvc.perform(MockMvcRequestBuilders.put(CONTROLLER_PATH + "/edit")
                        .with(user(currentUser))
                        .content(asJsonString(noteFormDTO))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        verify(noteService, times(0)).edit(noteFormDTO);
    }

    @Test
    public void edit_WhenNoteIdIsEmpty_ExpectNotFound() throws Exception {
        final NoteFormDTO noteFormDTO = createNoteFormDTO();
        noteFormDTO.setId(null);

        final User currentUser = createUser();
        currentUser.setRoles(List.of(Role.USER));

        final Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(currentUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final NoteWithLikeDataDTO existingNote = new NoteWithLikeDataDTO();
        existingNote.setAuthorId(currentUser.getId());

        mockMvc.perform(MockMvcRequestBuilders.put(CONTROLLER_PATH + "/edit")
                        .with(user(currentUser))
                        .content(asJsonString(noteFormDTO))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(noteService, times(0)).edit(noteFormDTO);
    }

    private User createUser(){
        User currentUser = new User();
        currentUser.setId("userId");
        currentUser.setUsername("username");
        currentUser.setPassword("password");

        return currentUser;
    }

    private NoteFormDTO createNoteFormDTO(){
        NoteFormDTO noteFormDTO = new NoteFormDTO();
        noteFormDTO.setId("noteId");
        noteFormDTO.setText("someText");

        return noteFormDTO;
    }

    private String asJsonString(final Object obj) throws JsonProcessingException {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
    }
}

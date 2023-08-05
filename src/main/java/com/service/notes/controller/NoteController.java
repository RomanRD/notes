package com.service.notes.controller;

import com.service.notes.domain.dto.NoteQueryDTO;
import com.service.notes.domain.dto.NoteFormDTO;
import com.service.notes.domain.dto.NoteWithLikeDataDTO;
import com.service.notes.model.Role;
import com.service.notes.persistence.entity.User;
import com.service.notes.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/all")
    public List<NoteWithLikeDataDTO> getAll(Authentication authentication){
        return noteService.getAll(getUserId(authentication));
    }

    @GetMapping()
    public List<NoteWithLikeDataDTO> getByQuery(@RequestBody NoteQueryDTO noteQuery, Authentication authentication){
        return noteService.getByQuery(noteQuery, getUserId(authentication));
    }


    @GetMapping("/{noteId}")
    public NoteWithLikeDataDTO getById(@PathVariable String noteId, Authentication authentication){
        return noteService.getById(noteId, getUserId(authentication));
    }

////    @GetMapping("/search")  //TODO implement
////    public List<NoteDTO> search(){
////        return noteService.search();
////    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_USER")
    public NoteWithLikeDataDTO create(@Valid @RequestBody NoteFormDTO noteFormDTO, Authentication authentication){
        noteFormDTO.setId(null);
        return noteService.create(noteFormDTO, getUserId(authentication));
    }

    @PutMapping("/edit")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public void edit(@Valid @RequestBody NoteFormDTO noteFormDTO, Authentication authentication){
        if(noteFormDTO.getId() == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        final User currentUser = (User) authentication.getPrincipal();
        final boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);
        if(!isAdmin &&
                !noteService.getById(noteFormDTO.getId(), null).getAuthorId().equals(currentUser.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        noteService.edit(noteFormDTO);
    }

    @PostMapping("/add-like/{id}")
    @Secured("ROLE_USER")
    public void addLike(@PathVariable String id, Authentication authentication){
        noteService.addLike(id, getUserId(authentication));
    }

    @DeleteMapping("/delete-like/{id}")
    @Secured("ROLE_USER")
    public void deleteLike(@PathVariable String id, Authentication authentication){
        noteService.deleteLike(id, getUserId(authentication));
    }

    @DeleteMapping("/delete/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public void delete(@PathVariable String id){
        noteService.delete(id);
    }

    private String getUserId(Authentication authentication){
        return authentication != null ? ((User)authentication.getPrincipal()).getId() : null;
    }

}

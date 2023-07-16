package com.service.notes.controller;

import com.service.notes.domain.dto.UserDTO;
import com.service.notes.domain.exception.UsernameAlreadyExists;
import com.service.notes.domain.exception.UsernameModificationNotAllowedException;
import com.service.notes.model.Role;
import com.service.notes.persistence.entity.User;
import com.service.notes.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @Secured("ROLE_ADMIN")
    public List<UserDTO> getAll(){
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public UserDTO get(@PathVariable(required = false) String id, HttpServletResponse response){
        final UserDTO user = userService.getById(id);

        if(user == null)
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        return user;
    }

//    @GetMapping("/search")  //TODO implement
//    public List<UserDTO> search(){
//        return userService.search();
//    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public UserDTO create(@Valid @RequestBody UserDTO user){
        try{
            return userService.create(user);
        }catch (UsernameAlreadyExists e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO register(@Valid @RequestBody UserDTO user){

        user.setRoles(List.of(Role.USER));

        try{
            return userService.create(user);
        }catch (UsernameAlreadyExists e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/edit")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public void edit(@Valid @RequestBody UserDTO user, Authentication authentication){
        final User currentUser = (User) authentication.getPrincipal();
        final boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);
        if(!user.getId().equals(currentUser.getId()) && !isAdmin){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        final UserDTO byId = userService.getById(user.getId());
        if(byId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if(!isAdmin){
            user.setRoles(null);
        }

        try{
            userService.edit(user);
        }catch (UsernameModificationNotAllowedException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Secured("ROLE_ADMIN")
    public void delete(@PathVariable String id){
        userService.delete(id);
    }

}

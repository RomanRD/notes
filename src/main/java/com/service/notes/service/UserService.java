package com.service.notes.service;

import com.service.notes.domain.dto.UserDTO;
import com.service.notes.domain.exception.UsernameAlreadyExists;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<UserDTO> getAll();

    UserDTO getById(String id);

    UserDTO create(UserDTO user) throws UsernameAlreadyExists;

    void edit(UserDTO user);

    void delete(String id);

}

package com.service.notes.service.impl;

import com.service.notes.domain.dto.UserDTO;
import com.service.notes.domain.exception.UsernameAlreadyExists;
import com.service.notes.domain.exception.UsernameModificationNotAllowedException;
import com.service.notes.persistence.entity.User;
import com.service.notes.persistence.repository.UserRepository;
import com.service.notes.service.NoteService;
import com.service.notes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.service.notes.util.Utility;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final NoteService noteService;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(user -> mapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getById(String id) {
        return userRepository.findById(id).map(user -> mapper.map(user, UserDTO.class)).orElse(null);
    }

    @Override
    public UserDTO create(UserDTO userDTO) throws UsernameAlreadyExists {
        if(userRepository.existsByUsername(userDTO.getUsername())){
            throw new UsernameAlreadyExists(userDTO.getUsername());
        }

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        final User save = userRepository.save(mapper.map(userDTO, User.class));

        logger.info("User " + save.getUsername() + " create by " + Utility.getCurrentUsername());

        return mapper.map(save, UserDTO.class);
    }

    @Override
    public void edit(UserDTO userDTO) {
        final UserDTO userById = getById(userDTO.getId());

        if(!userById.getUsername().equals(userDTO.getUsername())){
            throw new UsernameModificationNotAllowedException();
        }

        updatePresentUserFields(userById, userDTO);

        final User user = mapper.map(userById, User.class);
        userRepository.save(user);
        logger.info("User " + userDTO.getUsername() + " edited by " + Utility.getCurrentUsername());
    }

    private void updatePresentUserFields(UserDTO user, UserDTO updateData) {
        updateFieldIfPresent(updateData::getFirstName, user::setFirstName);
        updateFieldIfPresent(updateData::getSecondName, user::setSecondName);
        updateFieldIfPresent(updateData::getRoles, user::setRoles);
        updateFieldIfPresent(updateData::getPassword, (newPassword) -> user.setPassword(passwordEncoder.encode(newPassword)));
    }

    private <T> void updateFieldIfPresent(Supplier<T> fieldGetter, Consumer<T> fieldSetter) {
        Optional.ofNullable(fieldGetter.get()).ifPresent(fieldSetter);
    }

    @Override
    public void delete(String id) {
        userRepository.deleteById(id);
        logger.info("User " + id + " deleted by " + Utility.getCurrentUsername());

        noteService.deleteByAuthor(id);
    }

}

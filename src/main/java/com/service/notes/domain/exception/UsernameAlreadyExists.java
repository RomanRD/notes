package com.service.notes.domain.exception;

public class UsernameAlreadyExists extends RuntimeException {

    public UsernameAlreadyExists(String username) {
        super("User with the username " + username + " already exists");
    }
}

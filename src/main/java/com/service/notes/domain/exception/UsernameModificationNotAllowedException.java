package com.service.notes.domain.exception;

public class UsernameModificationNotAllowedException extends RuntimeException{

    public UsernameModificationNotAllowedException() {
        super("Forbidden to change the username");
    }

}

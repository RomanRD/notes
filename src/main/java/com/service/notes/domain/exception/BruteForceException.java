package com.service.notes.domain.exception;

public class BruteForceException extends Exception {

    public BruteForceException(int clientIP) {
        super("Client "+ clientIP +" has exceeded the number of attempts");
    }

}

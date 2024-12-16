package com.example.demo.exception;

public class InvalidChatParamException extends RuntimeException {
    public InvalidChatParamException(String message) {
        super(message);
    }
}

package com.example.demo.exception;

public class PineconeApiException extends RuntimeException {
    public PineconeApiException(String message) {
        super(message);
    }
}

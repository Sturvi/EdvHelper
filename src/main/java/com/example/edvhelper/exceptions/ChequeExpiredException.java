package com.example.edvhelper.exceptions;

public class ChequeExpiredException extends RuntimeException {

    public ChequeExpiredException(String message) {
        super(message);
    }
}

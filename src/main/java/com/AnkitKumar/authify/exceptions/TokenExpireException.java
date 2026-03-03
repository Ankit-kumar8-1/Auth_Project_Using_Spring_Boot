package com.AnkitKumar.authify.exceptions;

public class TokenExpireException extends  RuntimeException{
    public TokenExpireException(String message) {
        super(message);
    }
}

package com.AnkitKumar.authify.exceptions;

public class EmailNotVerifiedException extends  RuntimeException{
    public EmailNotVerifiedException(String message){
        super(message);
    }
}

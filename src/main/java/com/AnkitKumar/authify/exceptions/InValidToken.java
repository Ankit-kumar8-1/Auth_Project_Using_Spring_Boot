package com.AnkitKumar.authify.exceptions;

public class InValidToken extends  RuntimeException{
    public InValidToken(String message){
        super(message);
    }
}

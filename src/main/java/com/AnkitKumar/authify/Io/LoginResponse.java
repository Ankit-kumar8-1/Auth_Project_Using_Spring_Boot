package com.AnkitKumar.authify.Io;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private boolean success;
    private String message;
    private String email;
    private String fullName;
    private String role;
    private String token;
}
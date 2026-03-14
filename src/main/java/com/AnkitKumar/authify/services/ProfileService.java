package com.AnkitKumar.authify.services;

import com.AnkitKumar.authify.Io.LoginRequest;
import com.AnkitKumar.authify.Io.LoginResponse;
import com.AnkitKumar.authify.Io.ProfileRequest;
import com.AnkitKumar.authify.Io.ProfileResponse;
import org.springframework.stereotype.Service;


public interface ProfileService {

     ProfileResponse createProfile(ProfileRequest profileRequest);

     LoginResponse login(LoginRequest request);

     ProfileResponse getProfile(String email);

    void sendResetOtp();

    void resetPassword(String otp , String newPassword);


    Boolean isAuthenticated();

    void verifyUser(String token);
}

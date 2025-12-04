package com.AnkitKumar.authify.services;

import com.AnkitKumar.authify.Io.ProfileRequest;
import com.AnkitKumar.authify.Io.ProfileResponse;
import org.springframework.stereotype.Service;


public interface ProfileService {

     ProfileResponse createProfile(ProfileRequest profileRequest);

     ProfileResponse getProfile(String email);

    void sendResetOtp(String email);

    void resetPasword(String email, String otp , String newPassword);

    void sendOtp(String email);

    void verifyOtp(String email , String otp);

}

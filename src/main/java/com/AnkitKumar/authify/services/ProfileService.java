package com.AnkitKumar.authify.services;

import com.AnkitKumar.authify.Io.ProfileRequest;
import com.AnkitKumar.authify.Io.ProfileResponse;
import org.springframework.stereotype.Service;


public interface ProfileService {

     ProfileResponse createProfile(ProfileRequest profileRequest);

     ProfileResponse getProfile(String email);
}

package com.AnkitKumar.authify.controller;

import com.AnkitKumar.authify.Io.ApiResponse;
import com.AnkitKumar.authify.Io.ProfileRequest;
import com.AnkitKumar.authify.Io.ProfileResponse;
import com.AnkitKumar.authify.services.EmailService;
import com.AnkitKumar.authify.services.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ProfileController {

    private  final ProfileService profileService;
    private final EmailService emailService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProfileResponse>> register(@Valid  @RequestBody ProfileRequest request){
        ProfileResponse response = profileService.createProfile(request);
        ApiResponse<ProfileResponse> apiResponse =
                new ApiResponse<>(true, "Profile created successfully", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@RequestParam String token){
        profileService.verifyUser(token);
        ApiResponse<String> apiResponse = new ApiResponse<>(true,"Email Verified Successfully",null);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/profile")
    public  ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return  profileService.getProfile(email);
    }


}

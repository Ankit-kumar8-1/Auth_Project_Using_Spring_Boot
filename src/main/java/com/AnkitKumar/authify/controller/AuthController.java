package com.AnkitKumar.authify.controller;

import com.AnkitKumar.authify.Io.ApiResponse;
import com.AnkitKumar.authify.Io.LoginRequest;
import com.AnkitKumar.authify.Io.LoginResponse;
import com.AnkitKumar.authify.Io.ResetPasswordRequest;
import com.AnkitKumar.authify.services.AppUserDetailsService;
import com.AnkitKumar.authify.services.ProfileService;
import com.AnkitKumar.authify.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {


    private final ProfileService profileService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(profileService.login(request));
    }


    @GetMapping("/is-authenticated")
    public  ResponseEntity<ApiResponse<String>>  isAuthenticated(){
        Boolean result= profileService.isAuthenticated();
        if (result){
            return  ResponseEntity.status(HttpStatus.OK).body( new ApiResponse<>(true,"User is Authenticated",null));
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false,"User is NotAuthenticated",null));
        }
    }

    @PostMapping("/send-reset-otp")
    public  void  sendResetOtp(@RequestParam String email){
        try {
            profileService.sendResetOtp(email);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public  void resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        try{
            profileService.resetPassword(request.getEmail(),request.getOtp(),request.getNewPassword());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public  void  sendVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name") String email){
        try {
            profileService.sendOtp(email);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
    @PostMapping("/verify-otp")
    public  void VerifyEmail(@RequestBody Map<String , Object> request , @CurrentSecurityContext(expression = "authentication?.name") String email){

        if(request.get("otp").toString() ==null ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing Details");
        }

        try{
            profileService.verifyOtp(email,request.get("otp").toString());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
}

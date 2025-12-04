package com.AnkitKumar.authify.services;

import com.AnkitKumar.authify.Io.ProfileRequest;
import com.AnkitKumar.authify.Io.ProfileResponse;
import com.AnkitKumar.authify.Repositories.UserRepository;
import com.AnkitKumar.authify.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements   ProfileService{

    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    @Override
    public ProfileResponse createProfile(ProfileRequest request) {

        // 1. Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists!"
            );
        }

        // 2. Convert and save
        UserEntity newProfile = convertToEntity(request);
        newProfile = userRepository.save(newProfile);
        return convertToProfileResponse(newProfile);
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found : " + email ));

        return convertToProfileResponse(existingUser);
    }


    private  ProfileResponse convertToProfileResponse(UserEntity newProfile){
        return  ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }



    private UserEntity convertToEntity(ProfileRequest request){
        return  UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();
    }


    @Override
    public void sendResetOtp(String email) {
        UserEntity existsEntity = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found : " + email));

//        Generate 6 digit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

//        calculate expire  (current time + 15 minutes  in milliseconds )
        long  expiryTime = System.currentTimeMillis() + (15*60*1000);

//        update the profile / user
        existsEntity.setResetOtp(otp);
        existsEntity.setResetOtpExpireAt(expiryTime);
        userRepository.save(existsEntity);

        try{
//            send the reset otp email
            emailService.sendResetOtpEmail(existsEntity.getEmail(),otp);

        }catch (Exception ex){
            throw new RuntimeException("Unable to send email ");
        }
    }

    @Override
    public void resetPasword(String email, String otp, String newPassword) {
        UserEntity existingUser  = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found : "+ email));

        if(existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP !");
        }

        if (existingUser.getResetOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired  : ****** ");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepository.save(existingUser);
    }

    @Override
    public void sendOtp(String  email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()->  new UsernameNotFoundException("User not found : "+ email));

        if(existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()){
            return;
        }

//        Generate otp ;
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

//        calculate expire  (current time + 24 hours   in milliseconds )
        long  expiryTime = System.currentTimeMillis() + (24*60*60*1000);

//        Update the user  the entity

        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);

//        save to databases
        userRepository.save(existingUser);

        try {
            emailService.sendOtpEmail(existingUser.getEmail(),otp);
        }catch (Exception e){
            throw new RuntimeException("Unable to send email");
        }

    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity existingUser  = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found "+ email));

        if(existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if(existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired !");
        }

        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepository.save(existingUser);
    }



}

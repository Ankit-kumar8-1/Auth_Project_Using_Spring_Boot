package com.AnkitKumar.authify.services;

import com.AnkitKumar.authify.Io.ProfileRequest;
import com.AnkitKumar.authify.Io.ProfileResponse;
import com.AnkitKumar.authify.Repositories.UserRepository;
import com.AnkitKumar.authify.entities.UserEntity;
import com.AnkitKumar.authify.exceptions.EmailAlreadyExistsException;
import com.AnkitKumar.authify.exceptions.InValidToken;
import com.AnkitKumar.authify.exceptions.TokenExpireException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        if (userRepository.existsByEmail(request.getEmail())) {
            throw  new EmailAlreadyExistsException("Email Already exists!");
        }
        UserEntity newProfile = convertToEntity(request);
        newProfile.setPassword(passwordEncoder.encode(request.getPassword()));

        String token = UUID.randomUUID().toString();
        newProfile.setEmailVerificationToken(token);
        newProfile.setEmailVerificationTokenExpiry(LocalDateTime.now().plusMinutes(15));
        newProfile.setIsAccountVerified(false);

        newProfile = userRepository.save(newProfile);
        emailService.sendWelcomeEmail(newProfile.getEmail(),newProfile.getName(),newProfile.getEmailVerificationToken());
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
                .name(request.getName())
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
    public void resetPassword(String email, String otp, String newPassword) {
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

    @Override
    public void verifyUser(String token) {
        UserEntity user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InValidToken("Invalid Token "));


        if (user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())){
            throw new TokenExpireException("Token Expire");
        }


        user.setIsAccountVerified(true);
        user.setEmailVerificationTokenExpiry(null);
        user.setEmailVerificationToken(null);

        userRepository.save(user);
    }

}

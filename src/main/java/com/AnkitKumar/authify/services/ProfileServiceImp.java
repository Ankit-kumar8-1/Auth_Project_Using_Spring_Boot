package com.AnkitKumar.authify.services;

import com.AnkitKumar.authify.Io.LoginRequest;
import com.AnkitKumar.authify.Io.LoginResponse;
import com.AnkitKumar.authify.Io.ProfileRequest;
import com.AnkitKumar.authify.Io.ProfileResponse;
import com.AnkitKumar.authify.Repositories.UserRepository;
import com.AnkitKumar.authify.domain.Role;
import com.AnkitKumar.authify.entities.UserEntity;
import com.AnkitKumar.authify.exceptions.*;
import com.AnkitKumar.authify.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;


    @Override
    public ProfileResponse createProfile(ProfileRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw  new EmailAlreadyExistsException("Email Already exists!");
        }

        UserEntity newProfile = convertToEntity(request);
        newProfile.setPassword(passwordEncoder.encode(request.getPassword()));
        newProfile.setUserId(UUID.randomUUID().toString());

        if(request.getEmail().equals("ankit@mailinator.com")){
            newProfile.setRole(Role.ADMIN);
        }else {
            newProfile.setRole(Role.USER);
        }

        String token = UUID.randomUUID().toString();
        newProfile.setEmailVerificationToken(token);
        newProfile.setEmailVerificationTokenExpiry(LocalDateTime.now().plusMinutes(15));
        newProfile.setIsAccountVerified(false);

        newProfile = userRepository.save(newProfile);
        emailService.sendWelcomeEmail(newProfile.getEmail(),newProfile.getName(),newProfile.getEmailVerificationToken());
        return convertToProfileResponse(newProfile);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + request.getEmail())
                );


        if (!user.getIsAccountVerified()) {
            throw new EmailNotVerifiedException("Please verify your email first");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails =
                appUserDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        return new LoginResponse(
                true,
                "Login successful",
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                token
        );
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found : " + email ));

        return convertToProfileResponse(existingUser);
    }


    @Override
    public void sendResetOtp() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity existsEntity = userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found : " + email));

        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        long  expiryTime = System.currentTimeMillis() + (15*60*1000);

        existsEntity.setResetOtp(otp);
        existsEntity.setResetOtpExpireAt(expiryTime);
        userRepository.save(existsEntity);

        try{
            emailService.sendResetOtpEmail(existsEntity.getEmail(),otp);

        }catch (Exception ex){
            throw new RuntimeException("Unable to send email ");
        }
    }

    @Override
    public void resetPassword( String otp, String newPassword) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
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
    public void verifyUser(String token) {

        UserEntity user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InValidToken("Invalid Request"));

        if (user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())){
            throw new TokenExpireException("Token Expire");
        }

        user.setIsAccountVerified(true);
        user.setEmailVerificationTokenExpiry(null);
        user.setEmailVerificationToken(null);

        userRepository.save(user);
    }


    @Override
    public Boolean isAuthenticated() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getIsAccountVerified();
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

}

package com.AnkitKumar.authify.services;

import com.AnkitKumar.authify.Repositories.UserRepository;
import com.AnkitKumar.authify.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private  final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Email not found for the email "+ email));

        return new User(
                existingUser.getEmail(),
                existingUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + existingUser.getRole().name())));

    }


}

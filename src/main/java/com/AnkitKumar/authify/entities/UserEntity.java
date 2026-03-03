package com.AnkitKumar.authify.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "table_users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String userId;
    private String name ;
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    private String verifyOtp;


    @Column(nullable = false)
    private Boolean isAccountVerified = false;

    @Column(name = "emailVerification_token")
    private String emailVerificationToken;

    @Column(name = "emailVerificationToken_expiry")
    private LocalDateTime emailVerificationTokenExpiry;

    private Long verifyOtpExpireAt;
    private String resetOtp;
    private  Long resetOtpExpireAt;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private  Timestamp updatedAt;
}

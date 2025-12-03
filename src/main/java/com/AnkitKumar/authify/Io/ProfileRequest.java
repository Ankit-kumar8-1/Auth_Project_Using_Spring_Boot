package com.AnkitKumar.authify.Io;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class
ProfileRequest {


    @NotBlank(message = "name should be not empty")
    private String name;

    @Email(message = "Enter valid email address")
    @NotBlank(message = "Email should be not empty")
    private String email;

    @Size(min = 6 , message = "Password must be AtLeast 6 characters ")
    private String password;
}

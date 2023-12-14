package com.javaproj.library.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDto {
    @Size(min = 3, max = 10, message = "First name must contain 3-10 characters")
    private String firstName;
    @Size(min = 3, max = 10, message = "Last name must contain 3-10 characters")
    private String lastName;
    private String username;
    @Size(min = 5, max = 10, message = "Password must contain 5-10 characters")
    private String password;
    private String repeatPassword;
}
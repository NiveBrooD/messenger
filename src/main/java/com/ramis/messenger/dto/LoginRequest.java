package com.ramis.messenger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 3, max = 15, message = "Password must be between 3 and 15 characters.")
    private String password;
}

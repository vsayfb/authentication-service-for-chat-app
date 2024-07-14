package com.example.authentication_service.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class AuthPayload {

    @NotBlank
    @Size(min = 2, max = 18)
    private String username;

    @NotBlank
    @Size(min = 7, max = 40)
    private String password;
}

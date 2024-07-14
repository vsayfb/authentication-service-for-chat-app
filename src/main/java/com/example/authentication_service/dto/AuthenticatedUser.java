package com.example.authentication_service.dto;

import com.example.authentication_service.external.dto.UserDTO;

import lombok.Data;

@Data
public class AuthenticatedUser {

    private String token;
    private UserDTO.Data user;
}

package com.example.authentication_service.dto;

import lombok.Data;

@Data
public class AuthPayload {

    private String username;
    private String password;
}

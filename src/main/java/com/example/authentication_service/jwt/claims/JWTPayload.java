package com.example.authentication_service.jwt.claims;

import lombok.Data;

@Data
public class JWTPayload {
    private String id;
    private String username;
}

package com.example.authentication_service.jwt.claims;

import lombok.Data;

@Data
public class JWTClaims {

    private String username;
    private String iss;
    private long exp;
    private long iat;
    private String id;
}

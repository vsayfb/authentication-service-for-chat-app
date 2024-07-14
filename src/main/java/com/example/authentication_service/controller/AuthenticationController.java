package com.example.authentication_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.example.authentication_service.dto.AuthPayload;
import com.example.authentication_service.dto.AuthenticatedUser;
import com.example.authentication_service.dto.AuthenticatedUserDto;
import com.example.authentication_service.external.dto.UserDTO.Data;
import com.example.authentication_service.jwt.JWTSigner;
import com.example.authentication_service.jwt.claims.JWTPayload;
import com.example.authentication_service.service.AuthenticationService;
import com.example.authentication_service.swagger.LoginApiResponse;
import com.example.authentication_service.swagger.RegisterApiResponse;

import jakarta.validation.Valid;

import java.util.HashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final JWTSigner jwtSigner;

    public AuthenticationController(AuthenticationService authenticationService, JWTSigner jwtSigner) {
        this.authenticationService = authenticationService;
        this.jwtSigner = jwtSigner;
    }

    @LoginApiResponse
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthPayload authPayload) {

        try {
            Data user = authenticationService.authenticate(authPayload);

            JWTPayload jwtPayload = new JWTPayload();

            jwtPayload.setId(user.getId());
            jwtPayload.setUsername(user.getUsername());
            jwtPayload.setProfilePicture(user.getProfilePicture());

            String token = jwtSigner.sign(jwtPayload);

            AuthenticatedUser authenticatedUser = new AuthenticatedUser();

            authenticatedUser.setToken(token);
            authenticatedUser.setUser(user);

            return new ResponseEntity<>(new AuthenticatedUserDto(authenticatedUser), HttpStatus.OK);

        } catch (HttpClientErrorException | HttpServerErrorException e) {

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(e.getResponseBodyAsString(), headers, e.getStatusCode());

        }

    }

    @RegisterApiResponse
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid AuthPayload authPayload) {

        try {
            Data newUser = authenticationService.newUser(authPayload);

            JWTPayload jwtPayload = new JWTPayload();

            jwtPayload.setId(newUser.getId());
            jwtPayload.setUsername(newUser.getUsername());
            jwtPayload.setProfilePicture(newUser.getProfilePicture());

            String token = jwtSigner.sign(jwtPayload);

            AuthenticatedUser authenticatedUser = new AuthenticatedUser();

            authenticatedUser.setToken(token);
            authenticatedUser.setUser(newUser);

            return new ResponseEntity<>(new AuthenticatedUserDto(authenticatedUser), HttpStatus.CREATED);
        } catch (HttpClientErrorException | HttpServerErrorException e) {

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(e.getResponseBodyAsString(), headers, e.getStatusCode());

        }

    }

}

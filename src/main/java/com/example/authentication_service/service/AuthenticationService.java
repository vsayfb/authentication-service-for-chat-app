package com.example.authentication_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.authentication_service.dto.AuthPayload;
import com.example.authentication_service.external.dto.UserDTO;

import lombok.Setter;

@Service
@Setter
public class AuthenticationService {

    private final RestTemplate restTemplate;

    @Value("${user_service_new_user_url}")
    private String newUserEndpoint;

    @Value("${user_service_validate_user_url}")
    private String validateUserEndpoint;

    public AuthenticationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserDTO authenticate(AuthPayload payload) throws HttpClientErrorException, HttpServerErrorException {

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthPayload> httpEntity = new HttpEntity<>(payload);

        ResponseEntity<UserDTO> responseEntity = restTemplate.postForEntity(validateUserEndpoint,
                httpEntity,
                UserDTO.class);

        return responseEntity.getBody();
    }

    public UserDTO newUser(AuthPayload payload) throws HttpClientErrorException, HttpServerErrorException {

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthPayload> httpEntity = new HttpEntity<>(payload);

        ResponseEntity<UserDTO> responseEntity = restTemplate.postForEntity(newUserEndpoint,
                httpEntity,
                UserDTO.class);

        return responseEntity.getBody();
    }

}
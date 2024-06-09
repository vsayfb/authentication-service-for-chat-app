package com.example.authentication_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import com.example.authentication_service.dto.AuthPayload;
import com.example.authentication_service.external.dto.UserDTO.Data;
import com.example.authentication_service.jwt.JWTSigner;
import com.example.authentication_service.jwt.claims.JWTPayload;
import com.example.authentication_service.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
public class AuthenticationControllerTest {

    private String loginEndpoint = "/auth/login";
    private String registerEndpoint = "/auth/register";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private JWTSigner jwtSigner;

    @Nested
    class Login {

        @Test
        void shouldHandleEmptyBody() throws Exception {
            mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldHandleInvalidBody() throws Exception {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("a");
            authPayload.setPassword("a");

            when(authService.authenticate(any(AuthPayload.class)))
                    .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

            ObjectMapper mapper = new ObjectMapper();

            mockMvc.perform(
                    post(loginEndpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(authPayload)))
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldHandleExternalServerError() throws Exception {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("a");
            authPayload.setPassword("a");

            when(authService.authenticate(any(AuthPayload.class)))
                    .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

            ObjectMapper mapper = new ObjectMapper();

            mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(authPayload)))
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isBadGateway());
        }

        @Test
        void shouldReturnSignedJwtAndData() throws Exception {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("walter");
            authPayload.setPassword("heisenberg");

            Data dummyUserData = new Data();

            dummyUserData.setUsername(authPayload.getUsername());
            dummyUserData.setId("id");

            when(authService.authenticate(any(AuthPayload.class)))
                    .thenReturn(dummyUserData);

            String dummyJwt = "ey_signed_jwt";

            when(jwtSigner.sign(any(JWTPayload.class)))
                    .thenReturn(dummyJwt);

            ObjectMapper mapper = new ObjectMapper();

            mockMvc.perform(post(loginEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(authPayload)))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.data.token", Matchers.equalTo(dummyJwt)));
        }

    }

    @Nested
    class Register {

        @Test
        void shouldHandleEmptyBody() throws Exception {
            mockMvc.perform(post(registerEndpoint).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldHandleInvalidBody() throws Exception {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("a");
            authPayload.setPassword("a");

            when(authService.newUser(any(AuthPayload.class)))
                    .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

            ObjectMapper mapper = new ObjectMapper();

            mockMvc.perform(
                    post(registerEndpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(authPayload)))
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldHandleExternalServerError() throws Exception {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("a");
            authPayload.setPassword("a");

            when(authService.newUser(any(AuthPayload.class)))
                    .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

            ObjectMapper mapper = new ObjectMapper();

            mockMvc.perform(post(registerEndpoint).contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(authPayload)))
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isBadGateway());
        }

        @Test
        void shouldReturnSignedJwtAndData() throws Exception {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("walter");
            authPayload.setPassword("heisenberg");

            Data dummyUserData = new Data();

            dummyUserData.setUsername(authPayload.getUsername());
            dummyUserData.setId("id");

            when(authService.newUser(any(AuthPayload.class)))
                    .thenReturn(dummyUserData);

            String dummyJwt = "ey_signed_jwt";

            when(jwtSigner.sign(any(JWTPayload.class)))
                    .thenReturn(dummyJwt);

            ObjectMapper mapper = new ObjectMapper();

            mockMvc.perform(post(registerEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(authPayload)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.data.token", Matchers.equalTo(dummyJwt)))
                    .andExpect(jsonPath("$.data.user.id", Matchers.equalTo(dummyUserData.getId())))
                    .andExpect(jsonPath("$.data.user.username", Matchers.equalTo(authPayload.getUsername())));

        }

    }

}

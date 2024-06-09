package com.example.authentication_service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.authentication_service.dto.AuthPayload;
import com.example.authentication_service.external.dto.UserDTO;
import com.example.authentication_service.external.dto.UserDTO.Data;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthenticationService authenticationService;

    AuthPayload authPayload = new AuthPayload();

    private String newUserEndpoint = "http://user-service/users";
    private String validateUserEndpoint = "http://user-service/users/validate";

    @BeforeEach
    void beforeEach() {

        authenticationService.setNewUserEndpoint(newUserEndpoint);
        authenticationService.setValidateUserEndpoint(validateUserEndpoint);

        authPayload.setUsername("username");
        authPayload.setPassword("password");
    }

    @Nested
    class Authentication {

        @Test
        void shouldThrowHttpClientErrorException() {

            when(
                    restTemplate.postForEntity(eq(validateUserEndpoint), any(HttpEntity.class),
                            eq(UserDTO.class)))
                    .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

            assertThrows(HttpClientErrorException.class, () -> authenticationService.authenticate(authPayload));

        }

        @Test
        void shouldThrowHttpServerErrorException() {

            when(
                    restTemplate.postForEntity(eq(validateUserEndpoint), any(HttpEntity.class),
                            eq(UserDTO.class)))
                    .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));

            assertThrows(HttpServerErrorException.class, () -> authenticationService.authenticate(authPayload));

        }

        @Test
        void shouldReturnUserData() {

            Data userDummy = new Data();

            userDummy.setId("id");
            userDummy.setUsername("username");

            UserDTO userDTO = new UserDTO();

            userDTO.setData(userDummy);

            ResponseEntity<UserDTO> responseEntity = new ResponseEntity<>(userDTO, HttpStatus.OK);

            when(
                    restTemplate.postForEntity(eq(validateUserEndpoint), any(HttpEntity.class),
                            eq(UserDTO.class)))
                    .thenReturn(responseEntity);

            Data user = authenticationService.authenticate(authPayload);

            assertEquals(user, userDummy);
        }

    }

    @Nested
    class NewUser {

        @Test
        void shouldThrowHttpClientErrorException() {

            when(
                    restTemplate.postForEntity(eq(newUserEndpoint), any(HttpEntity.class),
                            eq(UserDTO.class)))
                    .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

            assertThrows(HttpClientErrorException.class, () -> authenticationService.newUser(authPayload));

        }

        @Test
        void shouldThrowHttpServerErrorException() {

            when(
                    restTemplate.postForEntity(eq(newUserEndpoint), any(HttpEntity.class),
                            eq(UserDTO.class)))
                    .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));

            assertThrows(HttpServerErrorException.class, () -> authenticationService.newUser(authPayload));

        }

        @Test
        void shouldReturnUserData() {

            Data userDummy = new Data();

            userDummy.setId("id");
            userDummy.setUsername("username");

            UserDTO userDTO = new UserDTO();

            userDTO.setData(userDummy);

            ResponseEntity<UserDTO> responseEntity = new ResponseEntity<>(userDTO, HttpStatus.CREATED);

            when(
                    restTemplate.postForEntity(eq(newUserEndpoint), any(HttpEntity.class),
                            eq(UserDTO.class)))
                    .thenReturn(responseEntity);

            Data newUser = authenticationService.newUser(authPayload);

            assertEquals(newUser, userDummy);
        }
    }

}
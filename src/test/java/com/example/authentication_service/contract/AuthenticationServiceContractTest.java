package com.example.authentication_service.contract;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.authentication_service.dto.AuthPayload;
import com.example.authentication_service.external.dto.UserDTO;
import com.example.authentication_service.service.AuthenticationService;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

@PactConsumerTest
public class AuthenticationServiceContractTest {

    @Nested
    class NewUser {

        private String createUserEndpoint = "/users";

        @Pact(consumer = "AuthenticationService", provider = "UserService")
        public RequestResponsePact successNewUserPact(PactDslWithProvider builder) {

            return builder
                    .given("valid body with non-existing user")
                    .uponReceiving("a request to create a user")
                    .path(createUserEndpoint).method("POST")
                    .matchHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                    .body(newJsonBody(json -> {
                        json.stringType("username", "string");
                        json.stringType("password", "string");
                    }).build())
                    .willRespondWith()
                    .status(HttpStatus.CREATED.value())
                    .body(newJsonBody(json -> {
                        json.object("data", obj -> {
                            obj.stringType("id", "string");
                            obj.stringType("username", "string");
                        });
                    }).build()).toPact();
        }

        @Test
        @PactTestFor(pactMethod = "successNewUserPact", pactVersion = PactSpecVersion.V3)
        void successNewUserPactTest(MockServer mockServer) {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("username");
            authPayload.setPassword("password");

            RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();

            AuthenticationService authService = new AuthenticationService(restTemplate);

            authService.setNewUserEndpoint(mockServer.getUrl() + createUserEndpoint);

            UserDTO.Data userDTO = authService.newUser(authPayload);

            assertThat(userDTO.getUsername()).isInstanceOf(String.class);
            assertThat(userDTO.getId()).isInstanceOf(String.class);
        }

        @Pact(consumer = "AuthenticationService", provider = "UserService")
        public RequestResponsePact invalidBodyNewUserPact(PactDslWithProvider builder) {

            return builder
                    .given("invalid body")
                    .uponReceiving("a request to create a user")
                    .path(createUserEndpoint).method("POST")
                    .matchHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                    .body(newJsonBody(json -> {
                        json.stringType("username", "*@-invalid-@*-username");
                        json.stringType("password", "*@-invalid-@*-password");
                    }).build())
                    .willRespondWith()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .toPact();
        }

        @Test
        @PactTestFor(pactMethod = "invalidBodyNewUserPact", pactVersion = PactSpecVersion.V3)
        void invalidBodyNewUserPactTest(MockServer mockServer) {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("*@username/");
            authPayload.setPassword("/.password-!");

            RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();

            AuthenticationService authService = new AuthenticationService(restTemplate);

            authService.setNewUserEndpoint(mockServer.getUrl() + createUserEndpoint);

            assertThrows(HttpClientErrorException.BadRequest.class, () -> authService.newUser(authPayload));
        }

        @Pact(consumer = "AuthenticationService", provider = "UserService")
        public RequestResponsePact newUserExistPact(PactDslWithProvider builder) {

            return builder
                    .given("valid body with existing user")
                    .uponReceiving("a request to create a user")
                    .path(createUserEndpoint).method("POST")
                    .matchHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                    .body(newJsonBody(json -> {
                        json.stringType("username", "existing_user");
                        json.stringType("password", "password");
                    }).build())
                    .willRespondWith()
                    .status(HttpStatus.FORBIDDEN.value())
                    .toPact();
        }

        @Test
        @PactTestFor(pactMethod = "newUserExistPact", pactVersion = PactSpecVersion.V3)
        void newUserExistPactTest(MockServer mockServer) {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("existing_user");
            authPayload.setPassword("password");

            RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();

            AuthenticationService authService = new AuthenticationService(restTemplate);

            authService.setNewUserEndpoint(mockServer.getUrl() + createUserEndpoint);

            assertThrows(HttpClientErrorException.Forbidden.class, () -> authService.newUser(authPayload));
        }
    }

    @Nested
    class Authenticate {
        private String validateUserEndpoint = "/users/validate";

        @Pact(consumer = "AuthenticationService", provider = "UserService")
        public RequestResponsePact successAuthenticationPact(PactDslWithProvider builder) {

            return builder
                    .given("valid body with valid credentials")
                    .uponReceiving("a request to authenticate a user")
                    .path(validateUserEndpoint).method("POST")
                    .matchHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                    .body(newJsonBody(json -> {
                        json.stringType("username", "string");
                        json.stringType("password", "string");
                    }).build())
                    .willRespondWith()
                    .status(HttpStatus.OK.value())
                    .body(newJsonBody(json -> {
                        json.object("data", obj -> {
                            obj.stringType("id", "string");
                            obj.stringType("username", "string");
                        });
                    }).build()).toPact();
        }

        @Test
        @PactTestFor(pactMethod = "successAuthenticationPact", pactVersion = PactSpecVersion.V3)
        void successAuthenticationPactTest(MockServer mockServer) {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("username");
            authPayload.setPassword("password");

            RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();

            AuthenticationService authService = new AuthenticationService(restTemplate);

            authService.setValidateUserEndpoint(mockServer.getUrl() + validateUserEndpoint);

            UserDTO.Data userDTO = authService.authenticate(authPayload);

            assertThat(userDTO.getUsername()).isInstanceOf(String.class);
            assertThat(userDTO.getId()).isInstanceOf(String.class);
        }

        @Pact(consumer = "AuthenticationService", provider = "UserService")
        public RequestResponsePact invalidBodyAuthenticationPact(PactDslWithProvider builder) {

            return builder
                    .given("invalid body")
                    .uponReceiving("a request to authenticate a user")
                    .path(validateUserEndpoint).method("POST")
                    .matchHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                    .body(newJsonBody(json -> {
                        json.stringType("username", "*@-invalid-@*-username");
                        json.stringType("password", "*@-invalid-@*-password");
                    }).build())
                    .willRespondWith()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .toPact();
        }

        @Test
        @PactTestFor(pactMethod = "invalidBodyAuthenticationPact", pactVersion = PactSpecVersion.V3)
        void invalidBodyAuthenticationPactTest(MockServer mockServer) {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("**!userna-me/");
            authPayload.setPassword("@!*password..");

            RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();

            AuthenticationService authService = new AuthenticationService(restTemplate);

            authService.setValidateUserEndpoint(mockServer.getUrl() + validateUserEndpoint);

            assertThrows(HttpClientErrorException.BadRequest.class, () -> authService.authenticate(authPayload));
        }

        @Pact(consumer = "AuthenticationService", provider = "UserService")
        public RequestResponsePact invalidCredentialsAuthenticationPact(PactDslWithProvider builder) {

            return builder
                    .given("invalid credentials")
                    .uponReceiving("a request to authenticate a user")
                    .path(validateUserEndpoint).method("POST")
                    .matchHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                    .body(newJsonBody(json -> {
                        json.stringType("username", "string");
                        json.stringType("password", "string");
                    }).build())
                    .willRespondWith()
                    .status(HttpStatus.FORBIDDEN.value())
                    .toPact();
        }

        @Test
        @PactTestFor(pactMethod = "invalidCredentialsAuthenticationPact", pactVersion = PactSpecVersion.V3)
        void invalidCredentialsAuthenticationPactTest(MockServer mockServer) {

            AuthPayload authPayload = new AuthPayload();

            authPayload.setUsername("wrong_username");
            authPayload.setPassword("password");

            RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();

            AuthenticationService authService = new AuthenticationService(restTemplate);

            authService.setValidateUserEndpoint(mockServer.getUrl() + validateUserEndpoint);

            assertThrows(HttpClientErrorException.Forbidden.class, () -> authService.authenticate(authPayload));
        }

    }

}

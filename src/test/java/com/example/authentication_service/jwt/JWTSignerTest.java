package com.example.authentication_service.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.authentication_service.jwt.claims.JWTClaims;
import com.example.authentication_service.jwt.claims.JWTPayload;

public class JWTSignerTest {

    private JWTSigner jwtSigner;

    private String base64URLEncoded = "NUU2MTFFNDUxREI4MTc4N0RGNUY3NThGMEFBMUExQUQ=";

    private JWTPayload payload;

    @BeforeEach
    void beforeEach() {
        jwtSigner = new JWTSigner(base64URLEncoded);

        jwtSigner.setSecretKey(base64URLEncoded);

        payload = new JWTPayload();

        payload.setId("1");
        payload.setUsername("joe");
    }

    @Nested
    class SignTests {

        @Test
        void shouldHandleNullPayload() {

            assertThrows(IllegalArgumentException.class, () -> jwtSigner.sign(null));
        }

        @Test
        void shouldSignJWT() {

            String signed = jwtSigner.sign(payload);

            assertTrue(signed.startsWith("ey"));

            assertTrue(signed.split("\\.").length == 3);
        }
    }

    @Nested
    class ValidateTokenTests {

        @Test
        void shouldHandleNullSubject() {
            assertThrows(IllegalArgumentException.class, () -> jwtSigner.validateToken(null));

        }

        @Test
        void shouldHandleEmptySubject() {
            assertThrows(IllegalArgumentException.class, () -> jwtSigner.validateToken(""));
        }

        @Test
        void shouldHandleSignatureExceptionAndReturnEmpty() throws InterruptedException {

            String signed = jwtSigner.sign(payload);

            jwtSigner.setSecretKey("A" + base64URLEncoded.substring(1));

            Optional<JWTClaims> optional = jwtSigner.validateToken(signed);

            assertTrue(optional.isEmpty());
        }

        @Test
        void shouldHandleExpiredTokenAndReturnEmpty() throws InterruptedException {

            jwtSigner.setExpireAfterMS((long) 100);

            String signed = jwtSigner.sign(payload);

            Thread.sleep(500);

            Optional<JWTClaims> optional = jwtSigner.validateToken(signed);

            assertTrue(optional.isEmpty());
        }

        @Test
        void shouldHandleInvalidCompateAndReturnEmpty() throws InterruptedException {

            Optional<JWTClaims> optional = jwtSigner.validateToken("signed");

            assertTrue(optional.isEmpty());
        }

        @Test
        void shouldParseJWT() {

            String signed = jwtSigner.sign(payload);

            Optional<JWTClaims> optional = jwtSigner.validateToken(signed);

            assertTrue(optional.isPresent());

            JWTClaims claims = optional.get();

            assertEquals(claims.getUsername(), payload.getUsername());
            assertEquals(claims.getIss(), "http://authentication-server");
            assertNotNull(claims.getExp());
            assertNotNull(claims.getIat());
        }

    }

}

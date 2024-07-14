package com.example.authentication_service.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.authentication_service.dto.AuthenticatedUserDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Operation(summary = "Register user")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User is registered.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticatedUserDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid username or password supplied", content = @Content),
        @ApiResponse(responseCode = "403", description = "Wrong username or password supplied", content = @Content) })
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterApiResponse {

}

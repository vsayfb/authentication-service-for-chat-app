package com.example.authentication_service.external.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Data data;

    @lombok.Data
    @NoArgsConstructor
    public static class Data {
        private String id;
        private String username;
        private String profilePicture;
        private Date createdAt;
    }

}

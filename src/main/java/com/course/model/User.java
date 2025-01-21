package com.course.model;

import lombok.*;

@Getter
@Setter
@Builder
public class User {
    private String username;
    private String password;
    private String role;
    private String metadata;
}

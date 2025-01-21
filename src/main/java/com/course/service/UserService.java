package com.course.service;

import com.course.dto.RegisterRequest;
import com.course.model.User;
import io.smallrye.mutiny.Uni;


public interface UserService {

    Uni<Void> register(RegisterRequest registerRequest);

    Uni<String> login(String username, String password);

    Uni<Void> addMetadata(String username, String metadata);

    Uni<User> getAccount(String username);
}

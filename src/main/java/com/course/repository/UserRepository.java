package com.course.repository;

import com.course.model.User;
import io.smallrye.mutiny.Uni;

public interface UserRepository {

    Uni<User> findByUsername(String username);

    Uni<Void> addUser(User user, String hashedPassword);

    Uni<Void> updateMetadata(String username, String metadata);

    Uni<User> findUser(String username);
}

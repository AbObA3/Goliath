package com.course.service.impl;

import com.course.dto.RegisterRequest;
import com.course.exceptions.ServiceException;
import com.course.model.User;
import com.course.repository.UserRepository;
import com.course.service.UserService;
import com.course.utils.JwtUtils;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    Logger logger;

    public Uni<String> login(String username, String password) {
        logger.infof("Authenticating user: %s", username);
        return userRepository.findByUsername(username)
                .onItem().transform(user -> {
                    if (user == null) {
                        logger.warnf("Authentication failed: user not found: %s", username);
                        throw new ServiceException("Invalid credentials");
                    }
                    if (!BCrypt.checkpw(password, user.getPassword())) {
                        logger.warnf("Authentication failed: invalid password for user: %s", username);
                        throw new ServiceException("Invalid credentials");
                    }
                    logger.infof("Authentication successful for user: %s", username);
                    return JwtUtils.generateToken(user.getUsername(), user.getRole());
                })
                .onFailure().invoke(ex -> logger.errorf("Authentication error for user: %s. Error: %s", username, ex.getMessage()));
    }

    public Uni<Void> register(RegisterRequest registerRequest) {
        String hashedPassword = BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt());
        logger.infof("Registering new user: %s", registerRequest.getUsername());
        User user = User
                .builder()
                .role(registerRequest.getRole())
                .metadata(registerRequest.getMetadata())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .build();
        return userRepository.addUser(user, hashedPassword)
                .onItem().invoke(() -> logger.infof("User registered successfully: %s", user.getUsername()));
    }

    public Uni<Void> addMetadata(String username, String metadata) {
        logger.infof("Adding metadata to user with ID: %d", username);
        return userRepository.updateMetadata(username, metadata)
                .onItem().invoke(() -> logger.infof("Metadata updated successfully for user ID: %d", username));
    }

    public Uni<User> getAccount(String username) {
        logger.infof("Retrieving account for user: %s", username);
        return userRepository.findUser(username)
                .onItem().invoke(() -> logger.infof("Account %s has successfully retrieved", username));
    }
}

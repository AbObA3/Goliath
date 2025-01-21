package com.course.resource;

import com.course.annotations.RolesAllowed;
import com.course.dto.LoginRequest;
import com.course.dto.RegisterRequest;
import com.course.service.UserService;
import com.course.utils.TokenBlacklist;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UserService userService;

    @Inject
    TokenBlacklist tokenBlacklist;

    @Inject
    Logger logger;

    @POST
    @Path("/login")
    public Uni<Response> login(LoginRequest loginRequest) {
        logger.infof("Login request received for user: %s", loginRequest.getUsername());
        return userService.login(loginRequest.getUsername(), loginRequest.getPassword())
                .onItem().transform(token -> Response.ok().entity(token).build())
                .onFailure().recoverWithItem(throwable -> {
                    // Логирование ошибки
                    logger.warnf("Login failed for user: %s. Error: %s", loginRequest.getUsername(), throwable.getMessage());
                    // Возвращаем ответ с ошибкой в виде строки
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Invalid credentials: " + throwable.getMessage())
                            .build();
                });
    }

    @POST
    @Path("/register")
    public Uni<Response> register(RegisterRequest registerRequest) {
        logger.infof("Register request received for user: %s", registerRequest.getUsername());
        return userService.register(registerRequest)
                .onItem().transform(unused -> Response.status(Response.Status.CREATED).build());
    }

    @POST
    @Path("/logout")
    @RolesAllowed({"USER", "ADMIN"})
    public Uni<Response> logout(@HeaderParam("Authorization") String authorization) {
        logger.info("Logout request received.");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Uni.createFrom().item(() -> Response.status(Response.Status.BAD_REQUEST).build());
        }

        String token = authorization.substring(7); // Убираем "Bearer "

        // Добавляем токен в черный список
        tokenBlacklist.addToken(token);
        logger.infof("Token revoked: %s", token);

        return Uni.createFrom().item(() -> Response.ok().build());
    }
}
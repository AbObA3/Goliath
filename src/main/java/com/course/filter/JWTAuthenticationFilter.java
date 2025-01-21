package com.course.filter;

import com.course.annotations.RolesAllowed;
import com.course.utils.JwtUtils;
import com.course.utils.TokenBlacklist;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    JwtUtils jwtUtils;

    @Inject
    TokenBlacklist tokenBlacklist;

    @Inject
    Logger logger;

    @Context
    ResourceInfo resourceInfo;

    private static final List<String> EXCLUDED_PATHS = List.of("/auth/login", "/auth/register");


    @Override
    public void filter(ContainerRequestContext requestContext) {

        String path = requestContext.getUriInfo().getPath();

        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            return;
        }

        String authorization = requestContext.getHeaderString("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header.");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = authorization.substring(7); // Убираем "Bearer "
        if (!jwtUtils.isTokenValid(token) || tokenBlacklist.isTokenRevoked(token)) {
            logger.warn("Invalid or revoked token.");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        RolesAllowed rolesAllowed = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class);
        if (rolesAllowed != null) {
            String role = jwtUtils.getRoleFromToken(token);
            List<String> allowedRoles = List.of(rolesAllowed.value());
            if (!allowedRoles.contains(role)) {
                logger.warn("User role not allowed to access this resource.");
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("You don't have access to this endpoint").build());
            }
        }
    }
}
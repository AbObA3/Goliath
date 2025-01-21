package com.course.resource;

import com.course.annotations.RolesAllowed;
import com.course.dto.MetadataRequest;
import com.course.service.UserService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Inject
    UserService userService;

    @Inject
    Logger logger;

    @GET
    @Path("/{username}")
    @RolesAllowed({"USER", "ADMIN"})
    public Uni<Response> getAccount(@PathParam("username") String username) {
        return userService.getAccount(username)
                .onItem().ifNotNull().transform(user -> Response.ok(user).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Path("/metadata")
    @RolesAllowed({"USER", "ADMIN"})
    public Uni<Response> addMetadata(MetadataRequest metadataRequest) {
        return userService.addMetadata(metadataRequest.getUsername(), metadataRequest.getMetadata())
                .onItem().transform(unused -> Response.status(Response.Status.ACCEPTED).build())
                .onFailure().recoverWithItem(() -> {
                    logger.errorf("Failed to add metadata: %s", metadataRequest.getUsername());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                });
    }
}

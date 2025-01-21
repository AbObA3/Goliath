package com.course.resource;

import com.course.annotations.RolesAllowed;
import com.course.dto.CourseRequest;
import com.course.model.Course;
import com.course.service.CourseService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseResource {

    @Inject
    CourseService courseService;

    @Inject
    Logger logger;

    /**
     * Get all courses with pagination.
     */
    @GET
    @RolesAllowed({"USER", "ADMIN"})
    public Uni<Response> getCourses(@QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("10") int size) {
        logger.infof("Fetching courses for page %d with size %d", page, size);
        return courseService.getCourses(page, size)
                .onItem().transform(courses -> Response.ok(courses).build())
                .onFailure().recoverWithItem(() -> {
                    logger.error("Error fetching courses.");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                });
    }

    /**
     * Get a course by ID.
     */
    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Uni<Response> getCourseById(@PathParam("id") Long id) {
        logger.infof("Fetching course with ID: %d", id);
        return courseService.getCourseById(id)
                .onItem().transform(course -> Response.ok(course).build())
                .onFailure().recoverWithItem(() -> {
                    logger.errorf("Course with ID %d not found.", id);
                    return Response.status(Response.Status.NOT_FOUND).build();
                });
    }

    /**
     * Add a new course.
     */
    @POST
    @RolesAllowed({"USER","ADMIN"})
    public Uni<Response> addCourse(CourseRequest courseRequest) {
        logger.infof("Adding new course: %s", courseRequest.getTitle());
        return courseService.addCourse(courseRequest)
                .onItem().transform(unused -> Response.status(Response.Status.CREATED).build())
                .onFailure().recoverWithItem(() -> {
                    logger.errorf("Failed to add course: %s", courseRequest.getTitle());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                });
    }
}

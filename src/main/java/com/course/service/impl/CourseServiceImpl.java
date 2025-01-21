package com.course.service.impl;

import com.course.dto.CourseRequest;
import com.course.model.Course;
import com.course.repository.CourseRepository;
import com.course.service.CourseService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;


@ApplicationScoped
public class CourseServiceImpl implements CourseService {

    @Inject
    CourseRepository courseRepository;

    @Inject
    Logger logger;

    @Override
    public Uni<List<Course>> getCourses(Integer page, Integer size) {
        int offset = (page - 1) * size;
        logger.infof("Fetching all courses from offset %d of size %d", offset, size);
        return courseRepository.getCourses(offset, size);
    }

    @Override
    public Uni<Course> getCourseById(Long id) {
        logger.infof("Fetching course with ID %d", id);
        return courseRepository.getCourseById(id);
    }

    @Override
    public Uni<Void> addCourse(CourseRequest courseRequest) {
        logger.infof("Adding course: %s", courseRequest.getTitle());
        return courseRepository.addCourse(Course
                        .builder()
                        .title(courseRequest.getTitle())
                        .description(courseRequest.getDescription())
                        .imageUrl(courseRequest.getImageUrl())
                        .metadata(courseRequest.getMetadata())
                        .build())
                .onItem().invoke(() -> logger.infof("Course added successfully: %s", courseRequest.getTitle()));
    }
}

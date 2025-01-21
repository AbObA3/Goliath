package com.course.service;

import com.course.dto.CourseRequest;
import com.course.model.Course;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

import java.util.List;

public interface CourseService {

    Uni<List<Course>> getCourses(Integer page, Integer size);

    Uni<Course> getCourseById(Long id);

    Uni<Void> addCourse(CourseRequest courseRequest);
}

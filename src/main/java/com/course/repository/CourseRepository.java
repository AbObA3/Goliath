package com.course.repository;


import com.course.model.Course;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

import java.util.List;

public interface CourseRepository {

    Uni<List<Course>> getCourses(int offset, int limit);

    Uni<Course> getCourseById(Long id);

    Uni<Void> addCourse(Course course);
}

package com.course.repository.impl;

import com.course.exceptions.ServiceException;
import com.course.model.Course;
import com.course.repository.CourseRepository;
import com.course.utils.JdbcUtils;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CourseRepositoryImpl implements CourseRepository {

    @Inject
    JdbcUtils jdbcUtils;


    @Override
    public Uni<List<Course>> getCourses(int offset, int limit) {
        return Uni.createFrom().item(Unchecked.supplier(() -> {
            List<Course> courses = new ArrayList<>();
            String sql = "SELECT * FROM goliath.courses LIMIT ? OFFSET ?";
            try (Connection connection = jdbcUtils.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, limit);
                preparedStatement.setInt(2, offset);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        courses.add(Course
                                .builder()
                                .id(resultSet.getLong("id"))
                                .title(resultSet.getString("title"))
                                .imageUrl(resultSet.getString("image_url"))
                                .description(resultSet.getString("description"))
                                .build());
                    }
                }
            } catch (SQLException e) {
                throw new ServiceException("Database error while retrieving courses", e);
            }
            return courses;
        })).runSubscriptionOn(jdbcUtils.getExecutorService());


    }

    @Override
    public Uni<Course> getCourseById(Long id) {
        return Uni.createFrom().item(Unchecked.supplier(() -> {
            String sql = "SELECT * FROM goliath.courses WHERE id = ?";
            try (Connection connection = jdbcUtils.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return Course.builder()
                                .id(resultSet.getLong("id"))
                                .title(resultSet.getString("title"))
                                .imageUrl(resultSet.getString("image_url"))
                                .description(resultSet.getString("description"))
                                .metadata(resultSet.getString("metadata"))
                                .build();
                    }
                }

            } catch (SQLException e) {
                throw new ServiceException("Database error while retrieving course by ID", e);
            }
            return null;
        })).runSubscriptionOn(jdbcUtils.getExecutorService());
    }

    @Override
    public Uni<Void> addCourse(Course course) {
        return Uni.createFrom().item(Unchecked.supplier(() -> {
            String sql = "INSERT INTO goliath.courses (title, description, image_url, metadata) VALUES (?, ?, ?, ?::jsonb)";
            try (Connection connection = jdbcUtils.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, course.getTitle());
                stmt.setString(2, course.getDescription());
                stmt.setString(3, course.getImageUrl());
                stmt.setString(4, course.getMetadata());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new ServiceException("Database error while adding course", e);
            }
            return null;
        })).runSubscriptionOn(jdbcUtils.getExecutorService()).replaceWithVoid();
    }
}

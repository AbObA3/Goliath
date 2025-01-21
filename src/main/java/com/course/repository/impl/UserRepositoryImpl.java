package com.course.repository.impl;

import com.course.exceptions.ServiceException;
import com.course.model.User;
import com.course.repository.UserRepository;
import com.course.utils.JdbcUtils;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {

    @Inject
    JdbcUtils jdbcUtils;

    @Override
    public Uni<User> findByUsername(String username) {
        return Uni.createFrom().item(Unchecked.supplier(() -> {
            String sql = "SELECT * FROM goliath.users WHERE username = ?";
            try (Connection connection = jdbcUtils.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return User.builder()
                                .username(resultSet.getString("username"))
                                .password(resultSet.getString("password"))
                                .role(resultSet.getString("role"))
                                .build();
                    }
                }
            } catch (SQLException e) {
                throw new ServiceException("Database error while finding user by username", e);
            }
            return null;
        })).runSubscriptionOn(jdbcUtils.getExecutorService());
    }

    @Override
    public Uni<Void> addUser(User user, String hashedPassword) {
        return Uni.createFrom().item(Unchecked.supplier(() -> {
            String sql = "INSERT INTO goliath.users (username, password, role, metadata) VALUES (?, ?, ?, ?::jsonb)";
            try (Connection connection = jdbcUtils.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql))
            {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, user.getRole());
                preparedStatement.setString(4, user.getMetadata());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new ServiceException("Database error while adding user", e);
            }
            return null;
        })).runSubscriptionOn(jdbcUtils.getExecutorService()).replaceWithVoid();
    }

    public Uni<Void> updateMetadata(String username, String metadata) {
        return Uni.createFrom().item(Unchecked.supplier(() -> {
            String sql = "UPDATE goliath.users SET metadata = ?::jsonb, updated_at = NOW() WHERE username = ?";
            try (Connection connection = jdbcUtils.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, metadata);
                stmt.setString(2, username);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new ServiceException("Database error while updating metadata", e);
            }
            return null;
        })).runSubscriptionOn(jdbcUtils.getExecutorService()).replaceWithVoid();
    }

    public Uni<User> findUser(String username) {
        return Uni.createFrom().item(Unchecked.supplier(() -> {
            String sql = "SELECT * FROM goliath.users WHERE username = ?";
            try (Connection connection = jdbcUtils.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return User.builder()
                                .username(resultSet.getString("username"))
                                .password(resultSet.getString("password"))
                                .role(resultSet.getString("role"))
                                .metadata(resultSet.getString("metadata"))
                                .build();
                    }
                }
            } catch (SQLException e) {
                throw new ServiceException("Database error while finding user by username", e);
            }
            return null;
        })).runSubscriptionOn(jdbcUtils.getExecutorService());
    }


}

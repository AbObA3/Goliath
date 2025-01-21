package com.course.utils;

import com.course.exceptions.ServiceException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class JdbcUtils {

    @Getter
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final DataSource dataSource;

    @Inject
    public JdbcUtils(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new ServiceException("Failed to get a database connection", e);
        }
    }

}

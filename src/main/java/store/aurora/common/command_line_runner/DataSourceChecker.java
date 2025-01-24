package store.aurora.common.command_line_runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class DataSourceChecker implements CommandLineRunner {

    private final DataSource dataSource;
    private static final Logger DB_LOG = LoggerFactory.getLogger("user-logger");

    @Override
    public void run(String... args) throws SQLException {

        DB_LOG.info("Using DataSource implementation: {}", dataSource.getClass().getName());
        try (Connection connection = dataSource.getConnection()) {
            DB_LOG.info("Connection details: {}", connection);
        }
    }
}
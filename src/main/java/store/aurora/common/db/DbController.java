package store.aurora.common.db;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@RestController
@RequestMapping("/db")
public class DbController {

    private final DataSource dataSource;
    private static final Logger DB_LOG = LoggerFactory.getLogger("user-logger");

    @GetMapping
    public String geDataSource() throws SQLException {
        DB_LOG.info(dataSource.getClass().getName());
        try (Connection connection = dataSource.getConnection()) {
            DB_LOG.info("Connection details: {}", connection);
            return connection.toString();
        }
    }

}
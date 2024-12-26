package store.aurora.common.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/db")
@Slf4j
public class DbController {

    private final DataSource dataSource;

    @GetMapping
    public String geDataSource() throws SQLException {
        log.info(dataSource.getConnection().toString());
        return dataSource.getConnection().toString();
    }

}
package store.aurora.key;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    private final KeyManager keyManager;
    private final DatabaseProperties databaseProperties;

    @Autowired
    public DatabaseConfig(KeyManager keyManager, DatabaseProperties databaseProperties) {
        this.keyManager = keyManager;
        this.databaseProperties = databaseProperties;
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(keyManager.keyStore(databaseProperties.getUrl()));
        dataSource.setUsername(keyManager.keyStore(databaseProperties.getUserName()));
        dataSource.setPassword(keyManager.keyStore(databaseProperties.getPassword()));

        dataSource.setInitialSize(databaseProperties.getInitialSize());
        dataSource.setMaxTotal(databaseProperties.getMaxTotal());
        dataSource.setMinIdle(databaseProperties.getMinIdle());
        dataSource.setMaxIdle(databaseProperties.getMaxIdle());
        dataSource.setMaxWaitMillis(databaseProperties.getMaxWait());

        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setTestWhileIdle(true);

        return dataSource;
    }
}
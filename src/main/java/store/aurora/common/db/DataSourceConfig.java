//package store.aurora.common.db;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
////@Configuration
//public class DataSourceConfig {
//
//    @Bean
//    public DataSource dataSource() {
//
//        BasicDataSource dataSource = new BasicDataSource();
//
//        // DBCP2 설정 dev
//        dataSource.setUrl("jdbc:h2:mem:bookstore;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("");
//        dataSource.setDriverClassName("org.h2.Driver");
//
//        dataSource.setInitialSize(5);
//        dataSource.setMaxTotal(20);
//        dataSource.setMaxIdle(10);
//        dataSource.setMinIdle(5);
//        dataSource.setValidationQuery("SELECT 1");
//        dataSource.setTestOnBorrow(true);
//        dataSource.setTestWhileIdle(true);
//
//        // DBCP2 설정 prod
////        dataSource.setUrl("jdbc:mysql://133.186.241.167/project_be8_5rora_db?useSSL=false&serverTimezone=UTC");
////        dataSource.setUsername("project_be8_5rora");
////        dataSource.setPassword("oMyVE2dkskrCXF@t");
////        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//
////        dataSource.setInitialSize(20); // 초기 생성되는 연결 수
////        dataSource.setMaxTotal(100); // 최대 연결 수 (전체 요청 처리량 기반)
////        dataSource.setMaxIdle(50); // 최대 유휴 연결 수 (자주 사용되지 않을 때의 최적 값)
////        dataSource.setMinIdle(20); // 최소 유휴 연결 수 (초기 연결 수와 동일하게 유지)
////        dataSource.setMaxWaitMillis(5000); // 연결 요청 대기 시간 (5초)
////        dataSource.setValidationQuery("SELECT 1"); // 연결 유효성 확인 쿼리
////        dataSource.setTestOnBorrow(true); // 연결 풀에서 가져올 때 유효성 검사
////        dataSource.setTestWhileIdle(true); // 유휴 상태에서 유효성 검사
////        dataSource.setTimeBetweenEvictionRunsMillis(10000); // 유휴 연결 검사 주기 (10초)
////        dataSource.setMinEvictableIdleTimeMillis(60000); // 유휴 연결 최소 유지 시간 (60초)
//
//        return dataSource;
//    }
//}
//

package store.aurora.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * @package : store.aurora.common.config
 * @name : OpenAPIConfiguration.java
 * @date : 2024-12-25 오후 3:34
 * @author : Flature
 * @version : 1.0.0
 */
@Configuration
public class OpenAPIConfiguration {

    private static final String API_NAME = "Book Shopping MAll";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "북 쇼핑몰의 shop api";

    @Bean
    public OpenAPI OpenAPIConfig() {
        return new OpenAPI()
                .info(new Info().title(API_NAME).description(API_DESCRIPTION).version(API_VERSION));
    }
}
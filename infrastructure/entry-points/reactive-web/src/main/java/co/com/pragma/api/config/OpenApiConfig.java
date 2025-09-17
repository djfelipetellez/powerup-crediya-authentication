package co.com.pragma.api.config;

import co.com.pragma.api.util.OpenApiUtil;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${springdoc.info.title}")
    private String title;

    @Value("${springdoc.info.description}")
    private String description;

    @Value("${springdoc.info.version}")
    private String version;

    @Value("${springdoc.info.contact.name}")
    private String contactName;

    @Value("${springdoc.info.contact.email}")
    private String contactEmail;

    @Bean
    public OpenAPI customOpenAPI() {
        return OpenApiUtil.createApiInfo(title, description, version, contactName, contactEmail);
    }
}
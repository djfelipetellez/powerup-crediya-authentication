package co.com.pragma.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.paths.auth")
@Getter
@Setter
public class AuthPath {

    private String login;
    private String validateToken;
    private String registerInitial;
}
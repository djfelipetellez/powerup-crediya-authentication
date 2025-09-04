package co.com.pragma.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class RolPath {

    private final String roles;

    public RolPath(@Value("${api.paths.roles}") String roles) {
        this.roles = roles;
    }
}
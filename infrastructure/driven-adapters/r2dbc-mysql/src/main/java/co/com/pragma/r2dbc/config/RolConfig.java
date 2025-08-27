package co.com.pragma.r2dbc.config;

import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.r2dbc.RolReactiveRepository;
import co.com.pragma.r2dbc.RolReactiveRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RolConfig {

    private final RolReactiveRepository rolReactiveRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public RolRepository rolRepository() {
        return new RolReactiveRepositoryAdapter(rolReactiveRepository, objectMapper);
    }
}
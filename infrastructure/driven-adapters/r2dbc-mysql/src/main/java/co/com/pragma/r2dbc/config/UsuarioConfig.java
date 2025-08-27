package co.com.pragma.r2dbc.config;

import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.r2dbc.RolReactiveRepository;
import co.com.pragma.r2dbc.UsuarioReactiveRepository;
import co.com.pragma.r2dbc.UsuarioReactiveRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UsuarioConfig {


    private final UsuarioReactiveRepository usuarioReactiveRepository;
    private final RolReactiveRepository rolReactiveRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public UsuarioRepository usuarioRepository() {
        return new UsuarioReactiveRepositoryAdapter(usuarioReactiveRepository, rolReactiveRepository, objectMapper);
    }
}

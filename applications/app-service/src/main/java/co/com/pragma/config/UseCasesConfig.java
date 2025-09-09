package co.com.pragma.config;

import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import co.com.pragma.usecase.auth.AuthorizationUseCase;
import co.com.pragma.usecase.auth.LoginAuthenticationUseCase;
import co.com.pragma.usecase.rol.RolUseCase;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "co.com.pragma.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {


    // Use case beans
    @Bean
    public RolUseCase rolUseCase(RolRepository rolRepository, LogGateway logGateway) {
        return new RolUseCase(rolRepository, logGateway);
    }

    @Bean
    public UsuarioUseCase usuarioUseCase(UsuarioRepository usuarioRepository, RolRepository rolRepository, UsuarioValidator usuarioValidator, UsuarioCredencialRepository usuarioCredencialRepository, LogGateway logGateway) {
        return new UsuarioUseCase(usuarioRepository, rolRepository, usuarioValidator, usuarioCredencialRepository, logGateway);
    }

    @Bean
    public LoginAuthenticationUseCase loginAuthenticationUseCase(UsuarioCredencialRepository usuarioCredencialRepository, UsuarioRepository usuarioRepository, LogGateway logGateway) {
        return new LoginAuthenticationUseCase(usuarioCredencialRepository, usuarioRepository, logGateway);
    }

    @Bean
    public AuthorizationUseCase authorizationUseCase(UsuarioRepository usuarioRepository, LogGateway logGateway) {
        return new AuthorizationUseCase(usuarioRepository, logGateway);
    }
}
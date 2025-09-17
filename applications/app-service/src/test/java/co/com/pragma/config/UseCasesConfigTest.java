package co.com.pragma.config;

import co.com.pragma.model.auth.gateways.AuthenticationGateway;
import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import co.com.pragma.usecase.auth.AuthorizationUseCase;
import co.com.pragma.usecase.auth.LoginAuthenticationUseCase;
import co.com.pragma.usecase.rol.RolUseCase;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class UseCasesConfigTest {

    @Test
    void rolUseCase_shouldCreateBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            RolUseCase rolUseCase = context.getBean(RolUseCase.class);

            assertNotNull(rolUseCase);
            assertInstanceOf(RolUseCase.class, rolUseCase);
        }
    }

    @Test
    void usuarioUseCase_shouldCreateBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            UsuarioUseCase usuarioUseCase = context.getBean(UsuarioUseCase.class);

            assertNotNull(usuarioUseCase);
            assertInstanceOf(UsuarioUseCase.class, usuarioUseCase);
        }
    }

    @Test
    void loginAuthenticationUseCase_shouldCreateBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            LoginAuthenticationUseCase loginAuthenticationUseCase = context.getBean(LoginAuthenticationUseCase.class);

            assertNotNull(loginAuthenticationUseCase);
            assertInstanceOf(LoginAuthenticationUseCase.class, loginAuthenticationUseCase);
        }
    }

    @Test
    void authorizationUseCase_shouldCreateBean() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            AuthorizationUseCase authorizationUseCase = context.getBean(AuthorizationUseCase.class);

            assertNotNull(authorizationUseCase);
            assertInstanceOf(AuthorizationUseCase.class, authorizationUseCase);
        }
    }

    @Configuration
    @Import({UseCasesConfig.class, ValidatorsConfig.class})
    static class TestConfig {

        @Bean
        public UsuarioRepository usuarioRepository() {
            return mock(UsuarioRepository.class);
        }

        @Bean
        public RolRepository rolRepository() {
            return mock(RolRepository.class);
        }

        @Bean
        public LogGateway logGateway() {
            return mock(LogGateway.class);
        }

        @Bean
        public UsuarioCredencialRepository usuarioCredencialRepository() {
            return mock(UsuarioCredencialRepository.class);
        }

        @Bean
        public AuthenticationGateway authenticationGateway() {
            return mock(AuthenticationGateway.class);
        }

        @Bean
        public UsuarioValidator usuarioValidator() {
            return mock(UsuarioValidator.class);
        }
    }
}
package co.com.pragma.config;

import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
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
    }
}
package co.com.pragma.usecase.usuario.validator;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioEmailValidatorTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioEmailValidator validator;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .email("test@test.com")
                .build();
    }

    @Test
    void validate_shouldComplete_whenEmailIsUnique() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, 1))
                .verifyComplete();
    }

    @Test
    void validate_shouldThrowException_whenEmailExists() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.just(new Usuario()));

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                        error.getMessage().equals(Constantes.MSG_EMAIL_DUPLICATE)
                )
                .verify();
    }

    
}

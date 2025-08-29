package co.com.pragma.usecase.usuario.validator;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LoggingGateway;
import co.com.pragma.model.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class UsuarioParameterValidatorTest {

    @Mock
    private LoggingGateway loggingGateway;

    @InjectMocks
    private UsuarioParameterValidator validator;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .email("test@test.com")
                .build();
    }

    @Test
    void validate_shouldComplete_whenAllParametersAreValid() {
        // Arrange
        Integer roleId = 1;

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, roleId))
                .verifyComplete();
    }

    @Test
    void validate_shouldThrowException_whenUsuarioIsNull() {
        // Arrange
        Integer roleId = 1;
        doNothing().when(loggingGateway).warn(any(), any(), any());

        // Act & Assert
        StepVerifier.create(validator.validate(null, roleId))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                        error.getMessage().equals(Constantes.MSG_USUARIO_NULL)
                )
                .verify();
    }

    @Test
    void validate_shouldThrowException_whenEmailIsNull() {
        // Arrange
        usuario.setEmail(null);
        Integer roleId = 1;
        doNothing().when(loggingGateway).warn(any(), any(), any());

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, roleId))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                        error.getMessage().equals(Constantes.MSG_EMAIL_EMPTY)
                )
                .verify();
    }

    @Test
    void validate_shouldThrowException_whenEmailIsEmpty() {
        // Arrange
        usuario.setEmail("  ");
        Integer roleId = 1;
        doNothing().when(loggingGateway).warn(any(), any(), any());

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, roleId))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                        error.getMessage().equals(Constantes.MSG_EMAIL_EMPTY)
                )
                .verify();
    }

    @Test
    void validate_shouldThrowException_whenRoleIdIsNull() {
        // Arrange
        doNothing().when(loggingGateway).warn(any(), any(), any());

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, null))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                        error.getMessage().equals(Constantes.MSG_ROLE_ID_NULL)
                )
                .verify();
    }
}
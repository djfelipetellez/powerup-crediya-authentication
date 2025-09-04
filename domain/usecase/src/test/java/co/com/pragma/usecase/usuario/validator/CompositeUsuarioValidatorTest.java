package co.com.pragma.usecase.usuario.validator;

import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompositeUsuarioValidatorTest {

    @Mock
    private UsuarioValidator validator1;
    @Mock
    private UsuarioValidator validator2;
    @Mock
    private UsuarioValidator validator3;

    private CompositeUsuarioValidator compositeValidator;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
    }

    @Test
    void validate_shouldComplete_whenAllValidatorsComplete() {
        // Arrange
        when(validator1.validate(usuario, 1)).thenReturn(Mono.empty());
        when(validator2.validate(usuario, 1)).thenReturn(Mono.empty());
        when(validator3.validate(usuario, 1)).thenReturn(Mono.empty());
        compositeValidator = new CompositeUsuarioValidator(List.of(validator1, validator2, validator3));

        // Act & Assert
        StepVerifier.create(compositeValidator.validate(usuario, 1))
                .verifyComplete();

        verify(validator1, times(1)).validate(usuario, 1);
        verify(validator2, times(1)).validate(usuario, 1);
        verify(validator3, times(1)).validate(usuario, 1);
    }

    @Test
    void validate_shouldFail_whenFirstValidatorFails() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Error from validator 1");
        when(validator1.validate(usuario, 1)).thenReturn(Mono.error(exception));
        compositeValidator = new CompositeUsuarioValidator(List.of(validator1, validator2, validator3));

        // Act & Assert
        StepVerifier.create(compositeValidator.validate(usuario, 1))
                .expectErrorMatches(error -> error == exception)
                .verify();

        verify(validator1, times(1)).validate(usuario, 1);
        verify(validator2, never()).validate(usuario, 1);
        verify(validator3, never()).validate(usuario, 1);
    }

    @Test
    void validate_shouldFail_whenSecondValidatorFails() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Error from validator 2");
        when(validator1.validate(usuario, 1)).thenReturn(Mono.empty());
        when(validator2.validate(usuario, 1)).thenReturn(Mono.error(exception));
        compositeValidator = new CompositeUsuarioValidator(List.of(validator1, validator2, validator3));

        // Act & Assert
        StepVerifier.create(compositeValidator.validate(usuario, 1))
                .expectErrorMatches(error -> error == exception)
                .verify();

        verify(validator1, times(1)).validate(usuario, 1);
        verify(validator2, times(1)).validate(usuario, 1);
        verify(validator3, never()).validate(usuario, 1);
    }

    @Test
    void validate_shouldHandleEmptyValidatorList() {
        // Arrange
        compositeValidator = new CompositeUsuarioValidator(List.of());

        // Act & Assert
        StepVerifier.create(compositeValidator.validate(usuario, 1))
                .verifyComplete();
    }
}

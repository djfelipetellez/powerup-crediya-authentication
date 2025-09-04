package co.com.pragma.usecase.usuario.validator;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.exceptions.BusinessRuleException;
import co.com.pragma.model.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class UsuarioSalaryValidatorTest {

    @InjectMocks
    private UsuarioSalaryValidator validator;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
    }

    @Test
    void validate_shouldComplete_whenSalaryIsValid() {
        // Arrange
        usuario.setSalarioBase(new BigDecimal("5000000"));

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, 1))
                .verifyComplete();
    }

    @Test
    void validate_shouldComplete_whenSalaryIsAtMinimum() {
        // Arrange
        usuario.setSalarioBase(new BigDecimal(Constantes.SALARY_MIN));

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, 1))
                .verifyComplete();
    }

    @Test
    void validate_shouldComplete_whenSalaryIsAtMaximum() {
        // Arrange
        usuario.setSalarioBase(new BigDecimal(Constantes.SALARY_MAX));

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, 1))
                .verifyComplete();
    }

    @Test
    void validate_shouldThrowException_whenSalaryIsNull() {
        // Arrange
        usuario.setSalarioBase(null);

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals(Constantes.MSG_SALARY_NULL)
                )
                .verify();
    }

    @Test
    void validate_shouldThrowException_whenSalaryIsBelowMinimum() {
        // Arrange
        usuario.setSalarioBase(new BigDecimal("-1"));
        String expectedMessage = Constantes.MSG_SALARY_RANGE + Constantes.SALARY_MIN + " y " + Constantes.SALARY_MAX;

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof BusinessRuleException &&
                                error.getMessage().equals(expectedMessage)
                )
                .verify();
    }

    @Test
    void validate_shouldThrowException_whenSalaryIsAboveMaximum() {
        // Arrange
        usuario.setSalarioBase(new BigDecimal(Constantes.SALARY_MAX).add(BigDecimal.ONE));
        String expectedMessage = Constantes.MSG_SALARY_RANGE + Constantes.SALARY_MIN + " y " + Constantes.SALARY_MAX;

        // Act & Assert
        StepVerifier.create(validator.validate(usuario, 1))
                .expectErrorMatches(error ->
                        error instanceof BusinessRuleException &&
                                error.getMessage().equals(expectedMessage)
                )
                .verify();
    }
}

package co.com.pragma.config;

import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import co.com.pragma.usecase.usuario.validator.CompositeUsuarioValidator;
import co.com.pragma.usecase.usuario.validator.UsuarioEmailValidator;
import co.com.pragma.usecase.usuario.validator.UsuarioSalaryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ValidatorsConfigTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private ValidatorsConfig validatorsConfig;

    @BeforeEach
    void setUp() {
        validatorsConfig = new ValidatorsConfig();
    }

    @Test
    void usuarioSalaryValidator_shouldCreateBean() {
        UsuarioSalaryValidator validator = validatorsConfig.usuarioSalaryValidator();

        assertNotNull(validator);
        assertInstanceOf(UsuarioSalaryValidator.class, validator);
    }

    @Test
    void usuarioEmailValidator_shouldCreateBean() {
        UsuarioEmailValidator validator = validatorsConfig.usuarioEmailValidator(usuarioRepository);

        assertNotNull(validator);
        assertInstanceOf(UsuarioEmailValidator.class, validator);
    }

    @Test
    void usuarioValidator_shouldCreateCompositeValidator() {
        UsuarioSalaryValidator salaryValidator = validatorsConfig.usuarioSalaryValidator();
        UsuarioEmailValidator emailValidator = validatorsConfig.usuarioEmailValidator(usuarioRepository);

        UsuarioValidator validator = validatorsConfig.usuarioValidator(
                salaryValidator,
                emailValidator
        );

        assertNotNull(validator);
        assertInstanceOf(CompositeUsuarioValidator.class, validator);
    }
}
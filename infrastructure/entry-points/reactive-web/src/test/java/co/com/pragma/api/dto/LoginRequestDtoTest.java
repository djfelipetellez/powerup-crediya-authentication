package co.com.pragma.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void constructor_withValidData_shouldCreateInstance() {
        String email = "test@example.com";
        String password = "password123";

        LoginRequestDto dto = new LoginRequestDto(email, password);

        assertEquals(email, dto.email());
        assertEquals(password, dto.password());
    }

    @Test
    void validation_withValidData_shouldPassValidation() {
        LoginRequestDto dto = new LoginRequestDto("user@domain.com", "validPassword");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_withNullEmail_shouldFailValidation() {
        LoginRequestDto dto = new LoginRequestDto(null, "password123");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El correo es obligatorio")));
    }

    @Test
    void validation_withEmptyEmail_shouldFailValidation() {
        LoginRequestDto dto = new LoginRequestDto("", "password123");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El correo es obligatorio")));
    }

    @Test
    void validation_withBlankEmail_shouldFailValidation() {
        LoginRequestDto dto = new LoginRequestDto("   ", "password123");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El correo es obligatorio")));
    }

    @Test
    void validation_withInvalidEmail_shouldFailValidation() {
        LoginRequestDto dto = new LoginRequestDto("invalid-email", "password123");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void validation_withNullPassword_shouldFailValidation() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", null);

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La contraseña es obligatoria")));
    }

    @Test
    void validation_withEmptyPassword_shouldFailValidation() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La contraseña es obligatoria")));
    }

    @Test
    void validation_withBlankPassword_shouldFailValidation() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "   ");

        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La contraseña es obligatoria")));
    }

    @Test
    void equals_withSameData_shouldBeEqual() {
        LoginRequestDto dto1 = new LoginRequestDto("test@example.com", "password");
        LoginRequestDto dto2 = new LoginRequestDto("test@example.com", "password");

        assertEquals(dto1, dto2);
    }

    @Test
    void equals_withDifferentEmail_shouldNotBeEqual() {
        LoginRequestDto dto1 = new LoginRequestDto("test1@example.com", "password");
        LoginRequestDto dto2 = new LoginRequestDto("test2@example.com", "password");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_withDifferentPassword_shouldNotBeEqual() {
        LoginRequestDto dto1 = new LoginRequestDto("test@example.com", "password1");
        LoginRequestDto dto2 = new LoginRequestDto("test@example.com", "password2");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void hashCode_withSameData_shouldBeSame() {
        LoginRequestDto dto1 = new LoginRequestDto("test@example.com", "password");
        LoginRequestDto dto2 = new LoginRequestDto("test@example.com", "password");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_shouldContainEmailButNotPassword() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "secretPassword");

        String result = dto.toString();

        assertTrue(result.contains("test@example.com"));
        assertTrue(result.contains("LoginRequestDto"));
    }

    @Test
    void validation_withValidEmailFormats_shouldPassValidation() {
        String[] validEmails = {
                "user@domain.com",
                "test.email@example.org",
                "user+tag@domain.co.uk",
                "123@numbers.com",
                "user_name@domain-name.com"
        };

        for (String email : validEmails) {
            LoginRequestDto dto = new LoginRequestDto(email, "password123");
            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "Email should be valid: " + email);
        }
    }

    @Test
    void validation_withInvalidEmailFormats_shouldFailValidation() {
        String[] invalidEmails = {
                "plainaddress",
                "@missingdomain.com",
                "missing@.com",
                "missing.domain@.com",
                "two@@domain.com",
                "domain.com@",
                ""
        };

        for (String email : invalidEmails) {
            LoginRequestDto dto = new LoginRequestDto(email, "password123");
            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "Email should be invalid: " + email);
        }
    }
}
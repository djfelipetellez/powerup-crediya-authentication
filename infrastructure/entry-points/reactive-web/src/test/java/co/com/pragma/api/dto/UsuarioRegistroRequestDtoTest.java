package co.com.pragma.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioRegistroRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void constructor_withValidData_shouldCreateInstance() {
        String nombre = "Juan";
        String apellido = "Pérez";
        String email = "juan.perez@example.com";
        String documentoIdentidad = "12345678";
        String telefono = "3001234567";
        BigDecimal salarioBase = new BigDecimal("2500000.00");
        String password = "password123";
        Integer idRol = 1;

        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                nombre, apellido, email, documentoIdentidad,
                telefono, salarioBase, password, idRol
        );

        assertEquals(nombre, dto.nombre());
        assertEquals(apellido, dto.apellido());
        assertEquals(email, dto.email());
        assertEquals(documentoIdentidad, dto.documentoIdentidad());
        assertEquals(telefono, dto.telefono());
        assertEquals(salarioBase, dto.salarioBase());
        assertEquals(password, dto.password());
        assertEquals(idRol, dto.idRol());
    }

    @Test
    void validation_withValidData_shouldPassValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "María", "García", "maria.garcia@company.com", "87654321",
                "3109876543", new BigDecimal("1800000"), "securePassword", 2
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_withNullNombre_shouldFailValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                null, "Pérez", "test@example.com", "12345678",
                "3001234567", new BigDecimal("2000000"), "password", 1
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El nombre es obligatorio")));
    }

    @Test
    void validation_withBlankNombre_shouldFailValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "   ", "Pérez", "test@example.com", "12345678",
                "3001234567", new BigDecimal("2000000"), "password", 1
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El nombre es obligatorio")));
    }

    @Test
    void validation_withNullApellido_shouldFailValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "Juan", null, "test@example.com", "12345678",
                "3001234567", new BigDecimal("2000000"), "password", 1
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El apellido es obligatorio")));
    }

    @Test
    void validation_withInvalidEmail_shouldFailValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "invalid-email", "12345678",
                "3001234567", new BigDecimal("2000000"), "password", 1
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void validation_withBlankEmail_shouldFailValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "   ", "12345678",
                "3001234567", new BigDecimal("2000000"), "password", 1
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El correo es obligatorio")));
    }

    @Test
    void validation_withBlankDocumentoIdentidad_shouldFailValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "test@example.com", "",
                "3001234567", new BigDecimal("2000000"), "password", 1
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El documento de identidad es obligatorio")));
    }

    @Test
    void validation_withNullSalarioBase_shouldFailValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "test@example.com", "12345678",
                "3001234567", null, "password", 1
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El salario base es obligatorio")));
    }

    @Test
    void validation_withBlankPassword_shouldFailValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "test@example.com", "12345678",
                "3001234567", new BigDecimal("2000000"), "", 1
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La contraseña es obligatoria")));
    }

    @Test
    void validation_withNullIdRol_shouldFailValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "test@example.com", "12345678",
                "3001234567", new BigDecimal("2000000"), "password", null
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("El ID del rol es obligatorio")));
    }

    @Test
    void validation_withNullTelefono_shouldPassValidation() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "test@example.com", "12345678",
                null, new BigDecimal("2000000"), "password", 1
        );

        Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void equals_withSameData_shouldBeEqual() {
        BigDecimal salario = new BigDecimal("2500000");

        UsuarioRegistroRequestDto dto1 = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "test@example.com", "12345678",
                "3001234567", salario, "password", 1
        );
        UsuarioRegistroRequestDto dto2 = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "test@example.com", "12345678",
                "3001234567", salario, "password", 1
        );

        assertEquals(dto1, dto2);
    }

    @Test
    void hashCode_withSameData_shouldBeSame() {
        BigDecimal salario = new BigDecimal("2500000");

        UsuarioRegistroRequestDto dto1 = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "test@example.com", "12345678",
                "3001234567", salario, "password", 1
        );
        UsuarioRegistroRequestDto dto2 = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "test@example.com", "12345678",
                "3001234567", salario, "password", 1
        );

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_shouldContainAllFieldsExceptPassword() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "Juan", "Pérez", "test@example.com", "12345678",
                "3001234567", new BigDecimal("2500000"), "secretPassword", 1
        );

        String result = dto.toString();

        assertTrue(result.contains("Juan"));
        assertTrue(result.contains("Pérez"));
        assertTrue(result.contains("test@example.com"));
        assertTrue(result.contains("UsuarioRegistroRequestDto"));
    }

    @Test
    void constructor_withSpecialCharacters_shouldAccept() {
        UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                "José María", "González-Rodríguez", "jose.maria@compañía.com",
                "CC-1.234.567-8", "+57-300-123-4567", new BigDecimal("2500000.50"),
                "P@ssw0rd!2023", 2
        );

        assertEquals("José María", dto.nombre());
        assertEquals("González-Rodríguez", dto.apellido());
        assertEquals("jose.maria@compañía.com", dto.email());
        assertEquals("CC-1.234.567-8", dto.documentoIdentidad());
        assertEquals("+57-300-123-4567", dto.telefono());
        assertEquals(new BigDecimal("2500000.50"), dto.salarioBase());
        assertEquals("P@ssw0rd!2023", dto.password());
        assertEquals(2, dto.idRol());
    }

    @Test
    void validation_withDifferentRoleIds_shouldPassValidation() {
        Integer[] roleIds = {1, 2, 3, 999};

        for (Integer roleId : roleIds) {
            UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                    "Test", "User", "test@example.com", "12345678",
                    "3001234567", new BigDecimal("2000000"), "password", roleId
            );

            Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "Role ID should be valid: " + roleId);
        }
    }

    @Test
    void validation_withDifferentSalaryAmounts_shouldPassValidation() {
        BigDecimal[] salaries = {
                new BigDecimal("1000000"),
                new BigDecimal("2500000.50"),
                new BigDecimal("999999999.99"),
                BigDecimal.ZERO
        };

        for (BigDecimal salary : salaries) {
            UsuarioRegistroRequestDto dto = new UsuarioRegistroRequestDto(
                    "Test", "User", "test@example.com", "12345678",
                    "3001234567", salary, "password", 1
            );

            Set<ConstraintViolation<UsuarioRegistroRequestDto>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "Salary should be valid: " + salary);
        }
    }
}
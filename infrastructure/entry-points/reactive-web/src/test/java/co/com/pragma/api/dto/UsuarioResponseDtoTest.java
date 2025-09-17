package co.com.pragma.api.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioResponseDtoTest {

    @Test
    void constructor_withValidData_shouldCreateInstance() {
        Integer idUsuario = 1;
        String nombre = "Juan";
        String apellido = "Pérez";
        String email = "juan.perez@example.com";
        String documentoIdentidad = "12345678";
        String telefono = "3001234567";
        BigDecimal salarioBase = new BigDecimal("2500000.00");
        RoleResponseDto rol = new RoleResponseDto(1, "ADMIN", "Administrador");

        UsuarioResponseDto dto = new UsuarioResponseDto(
                idUsuario, nombre, apellido, email, documentoIdentidad,
                telefono, salarioBase, rol
        );

        assertEquals(idUsuario, dto.idUsuario());
        assertEquals(nombre, dto.nombre());
        assertEquals(apellido, dto.apellido());
        assertEquals(email, dto.email());
        assertEquals(documentoIdentidad, dto.documentoIdentidad());
        assertEquals(telefono, dto.telefono());
        assertEquals(salarioBase, dto.salarioBase());
        assertEquals(rol, dto.rol());
    }

    @Test
    void constructor_withNullValues_shouldAcceptNull() {
        UsuarioResponseDto dto = new UsuarioResponseDto(
                null, null, null, null, null, null, null, null
        );

        assertNull(dto.idUsuario());
        assertNull(dto.nombre());
        assertNull(dto.apellido());
        assertNull(dto.email());
        assertNull(dto.documentoIdentidad());
        assertNull(dto.telefono());
        assertNull(dto.salarioBase());
        assertNull(dto.rol());
    }

    @Test
    void constructor_withPartialData_shouldAccept() {
        Integer idUsuario = 2;
        String nombre = "María";
        String apellido = "García";
        String email = "maria.garcia@company.com";
        RoleResponseDto rol = new RoleResponseDto(2, "ASESOR", "Asesor bancario");

        UsuarioResponseDto dto = new UsuarioResponseDto(
                idUsuario, nombre, apellido, email, null, null, null, rol
        );

        assertEquals(idUsuario, dto.idUsuario());
        assertEquals(nombre, dto.nombre());
        assertEquals(apellido, dto.apellido());
        assertEquals(email, dto.email());
        assertNull(dto.documentoIdentidad());
        assertNull(dto.telefono());
        assertNull(dto.salarioBase());
        assertEquals(rol, dto.rol());
    }

    @Test
    void equals_withSameData_shouldBeEqual() {
        BigDecimal salario = new BigDecimal("2500000.00");
        RoleResponseDto rol = new RoleResponseDto(1, "ADMIN", "Administrador");

        UsuarioResponseDto dto1 = new UsuarioResponseDto(
                1, "Juan", "Pérez", "juan.perez@example.com",
                "12345678", "3001234567", salario, rol
        );
        UsuarioResponseDto dto2 = new UsuarioResponseDto(
                1, "Juan", "Pérez", "juan.perez@example.com",
                "12345678", "3001234567", salario, rol
        );

        assertEquals(dto1, dto2);
    }

    @Test
    void equals_withDifferentIdUsuario_shouldNotBeEqual() {
        RoleResponseDto rol = new RoleResponseDto(1, "ADMIN", "Administrador");

        UsuarioResponseDto dto1 = new UsuarioResponseDto(
                1, "Juan", "Pérez", "juan.perez@example.com",
                "12345678", "3001234567", new BigDecimal("2500000"), rol
        );
        UsuarioResponseDto dto2 = new UsuarioResponseDto(
                2, "Juan", "Pérez", "juan.perez@example.com",
                "12345678", "3001234567", new BigDecimal("2500000"), rol
        );

        assertNotEquals(dto1, dto2);
    }

    @Test
    void hashCode_withSameData_shouldBeSame() {
        BigDecimal salario = new BigDecimal("2500000.00");
        RoleResponseDto rol = new RoleResponseDto(1, "ADMIN", "Administrador");

        UsuarioResponseDto dto1 = new UsuarioResponseDto(
                1, "Juan", "Pérez", "juan.perez@example.com",
                "12345678", "3001234567", salario, rol
        );
        UsuarioResponseDto dto2 = new UsuarioResponseDto(
                1, "Juan", "Pérez", "juan.perez@example.com",
                "12345678", "3001234567", salario, rol
        );

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_shouldContainAllFields() {
        Integer idUsuario = 1;
        String nombre = "Juan";
        String apellido = "Pérez";
        String email = "juan.perez@example.com";
        RoleResponseDto rol = new RoleResponseDto(1, "ADMIN", "Administrador");

        UsuarioResponseDto dto = new UsuarioResponseDto(
                idUsuario, nombre, apellido, email,
                "12345678", "3001234567", new BigDecimal("2500000"), rol
        );
        String result = dto.toString();

        assertTrue(result.contains("1"));
        assertTrue(result.contains("Juan"));
        assertTrue(result.contains("Pérez"));
        assertTrue(result.contains("juan.perez@example.com"));
        assertTrue(result.contains("UsuarioResponseDto"));
    }

    @Test
    void constructor_withDifferentRoles_shouldAccept() {
        RoleResponseDto adminRole = new RoleResponseDto(1, "ADMIN", "Administrador");
        RoleResponseDto asesorRole = new RoleResponseDto(2, "ASESOR", "Asesor");
        RoleResponseDto clienteRole = new RoleResponseDto(3, "CLIENTE", "Cliente");

        UsuarioResponseDto adminUser = new UsuarioResponseDto(
                1, "Admin", "User", "admin@test.com",
                "11111111", "3001111111", new BigDecimal("5000000"), adminRole
        );

        UsuarioResponseDto asesorUser = new UsuarioResponseDto(
                2, "Asesor", "User", "asesor@test.com",
                "22222222", "3002222222", new BigDecimal("3000000"), asesorRole
        );

        UsuarioResponseDto clienteUser = new UsuarioResponseDto(
                3, "Cliente", "User", "cliente@test.com",
                "33333333", "3003333333", new BigDecimal("2000000"), clienteRole
        );

        assertEquals(adminRole, adminUser.rol());
        assertEquals(asesorRole, asesorUser.rol());
        assertEquals(clienteRole, clienteUser.rol());
    }

    @Test
    void salarioBase_withDifferentPrecisions_shouldMaintainValue() {
        BigDecimal salarioConDecimales = new BigDecimal("2500000.50");
        BigDecimal salarioSinDecimales = new BigDecimal("3000000");
        BigDecimal salarioGrande = new BigDecimal("999999999.99");

        RoleResponseDto rol = new RoleResponseDto(1, "ADMIN", "Admin");

        UsuarioResponseDto dto1 = new UsuarioResponseDto(
                1, "User1", "Test", "user1@test.com",
                "11111111", "3001111111", salarioConDecimales, rol
        );

        UsuarioResponseDto dto2 = new UsuarioResponseDto(
                2, "User2", "Test", "user2@test.com",
                "22222222", "3002222222", salarioSinDecimales, rol
        );

        UsuarioResponseDto dto3 = new UsuarioResponseDto(
                3, "User3", "Test", "user3@test.com",
                "33333333", "3003333333", salarioGrande, rol
        );

        assertEquals(salarioConDecimales, dto1.salarioBase());
        assertEquals(salarioSinDecimales, dto2.salarioBase());
        assertEquals(salarioGrande, dto3.salarioBase());
    }

    @Test
    void constructor_withSpecialCharacters_shouldAccept() {
        String nombreEspecial = "José María";
        String apellidoEspecial = "González-Rodríguez";
        String emailEspecial = "jose.maria@compañía.com";
        String documentoEspecial = "CC-1.234.567-8";
        String telefonoEspecial = "+57-300-123-4567";
        BigDecimal salarioEspecial = new BigDecimal("2500000.75");
        RoleResponseDto rolEspecial = new RoleResponseDto(1, "ADMINISTRADOR", "Administrador del sistema");

        UsuarioResponseDto dto = new UsuarioResponseDto(
                1, nombreEspecial, apellidoEspecial, emailEspecial,
                documentoEspecial, telefonoEspecial, salarioEspecial, rolEspecial
        );

        assertEquals(nombreEspecial, dto.nombre());
        assertEquals(apellidoEspecial, dto.apellido());
        assertEquals(emailEspecial, dto.email());
        assertEquals(documentoEspecial, dto.documentoIdentidad());
        assertEquals(telefonoEspecial, dto.telefono());
        assertEquals(salarioEspecial, dto.salarioBase());
        assertEquals(rolEspecial, dto.rol());
    }

    @Test
    void constructor_withEmptyStrings_shouldAccept() {
        RoleResponseDto rol = new RoleResponseDto(1, "", "");

        UsuarioResponseDto dto = new UsuarioResponseDto(
                0, "", "", "", "", "", BigDecimal.ZERO, rol
        );

        assertEquals(0, dto.idUsuario());
        assertEquals("", dto.nombre());
        assertEquals("", dto.apellido());
        assertEquals("", dto.email());
        assertEquals("", dto.documentoIdentidad());
        assertEquals("", dto.telefono());
        assertEquals(BigDecimal.ZERO, dto.salarioBase());
        assertEquals(rol, dto.rol());
    }

    @Test
    void equals_withNullValues_shouldWork() {
        UsuarioResponseDto dto1 = new UsuarioResponseDto(
                null, null, null, null, null, null, null, null
        );
        UsuarioResponseDto dto2 = new UsuarioResponseDto(
                null, null, null, null, null, null, null, null
        );
        UsuarioResponseDto dto3 = new UsuarioResponseDto(
                1, null, null, null, null, null, null, null
        );

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    void hashCode_withNullValues_shouldNotThrow() {
        UsuarioResponseDto dto = new UsuarioResponseDto(
                null, null, null, null, null, null, null, null
        );

        assertDoesNotThrow(dto::hashCode);
    }

    @Test
    void toString_withNullValues_shouldNotThrow() {
        UsuarioResponseDto dto = new UsuarioResponseDto(
                null, null, null, null, null, null, null, null
        );

        assertDoesNotThrow(() -> {
            String result = dto.toString();
            assertTrue(result.contains("UsuarioResponseDto"));
        });
    }

    @Test
    void constructor_withLargeValues_shouldAccept() {
        Integer largeId = Integer.MAX_VALUE;
        String longName = "a".repeat(100);
        String longLastName = "b".repeat(100);
        String longEmail = "very-long-email-address@very-long-domain-name.com";
        String longDocument = "CC-" + "1".repeat(50);
        String longPhone = "+57-" + "3".repeat(15);
        BigDecimal largeSalary = new BigDecimal("999999999999.99");
        RoleResponseDto largeRole = new RoleResponseDto(
                Integer.MAX_VALUE,
                "ROLE_WITH_VERY_LONG_NAME",
                "Role with a very long description that spans multiple lines"
        );

        UsuarioResponseDto dto = new UsuarioResponseDto(
                largeId, longName, longLastName, longEmail,
                longDocument, longPhone, largeSalary, largeRole
        );

        assertEquals(largeId, dto.idUsuario());
        assertEquals(longName, dto.nombre());
        assertEquals(longLastName, dto.apellido());
        assertEquals(longEmail, dto.email());
        assertEquals(longDocument, dto.documentoIdentidad());
        assertEquals(longPhone, dto.telefono());
        assertEquals(largeSalary, dto.salarioBase());
        assertEquals(largeRole, dto.rol());
    }
}
package co.com.pragma.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleResponseDtoTest {

    @Test
    void constructor_withValidData_shouldCreateInstance() {
        Integer idRol = 1;
        String nombre = "ADMIN";
        String descripcion = "Administrador del sistema";
        
        RoleResponseDto dto = new RoleResponseDto(idRol, nombre, descripcion);
        
        assertEquals(idRol, dto.idRol());
        assertEquals(nombre, dto.nombre());
        assertEquals(descripcion, dto.descripcion());
    }

    @Test
    void constructor_withNullValues_shouldAcceptNull() {
        RoleResponseDto dto = new RoleResponseDto(null, null, null);
        
        assertNull(dto.idRol());
        assertNull(dto.nombre());
        assertNull(dto.descripcion());
    }

    @Test
    void constructor_withDifferentRoleTypes_shouldAccept() {
        RoleResponseDto adminDto = new RoleResponseDto(1, "ADMIN", "Administrador del sistema");
        RoleResponseDto asesorDto = new RoleResponseDto(2, "ASESOR", "Asesor del banco");
        RoleResponseDto clienteDto = new RoleResponseDto(3, "CLIENTE", "Cliente del banco");
        
        assertEquals(1, adminDto.idRol());
        assertEquals("ADMIN", adminDto.nombre());
        assertEquals("Administrador del sistema", adminDto.descripcion());
        
        assertEquals(2, asesorDto.idRol());
        assertEquals("ASESOR", asesorDto.nombre());
        assertEquals("Asesor del banco", asesorDto.descripcion());
        
        assertEquals(3, clienteDto.idRol());
        assertEquals("CLIENTE", clienteDto.nombre());
        assertEquals("Cliente del banco", clienteDto.descripcion());
    }

    @Test
    void equals_withSameData_shouldBeEqual() {
        Integer idRol = 1;
        String nombre = "ADMIN";
        String descripcion = "Administrador del sistema";
        
        RoleResponseDto dto1 = new RoleResponseDto(idRol, nombre, descripcion);
        RoleResponseDto dto2 = new RoleResponseDto(idRol, nombre, descripcion);
        
        assertEquals(dto1, dto2);
    }

    @Test
    void equals_withDifferentIdRol_shouldNotBeEqual() {
        RoleResponseDto dto1 = new RoleResponseDto(1, "ADMIN", "Administrador");
        RoleResponseDto dto2 = new RoleResponseDto(2, "ADMIN", "Administrador");
        
        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_withDifferentNombre_shouldNotBeEqual() {
        RoleResponseDto dto1 = new RoleResponseDto(1, "ADMIN", "Administrador");
        RoleResponseDto dto2 = new RoleResponseDto(1, "ASESOR", "Administrador");
        
        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_withDifferentDescripcion_shouldNotBeEqual() {
        RoleResponseDto dto1 = new RoleResponseDto(1, "ADMIN", "Administrador del sistema");
        RoleResponseDto dto2 = new RoleResponseDto(1, "ADMIN", "Administrador de la aplicación");
        
        assertNotEquals(dto1, dto2);
    }

    @Test
    void hashCode_withSameData_shouldBeSame() {
        Integer idRol = 1;
        String nombre = "ADMIN";
        String descripcion = "Administrador del sistema";
        
        RoleResponseDto dto1 = new RoleResponseDto(idRol, nombre, descripcion);
        RoleResponseDto dto2 = new RoleResponseDto(idRol, nombre, descripcion);
        
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_shouldContainAllFields() {
        Integer idRol = 1;
        String nombre = "ADMIN";
        String descripcion = "Administrador del sistema";
        
        RoleResponseDto dto = new RoleResponseDto(idRol, nombre, descripcion);
        String result = dto.toString();
        
        assertTrue(result.contains("1"));
        assertTrue(result.contains("ADMIN"));
        assertTrue(result.contains("Administrador del sistema"));
        assertTrue(result.contains("RoleResponseDto"));
    }

    @Test
    void idRol_shouldReturnCorrectValue() {
        Integer idRol = 999;
        RoleResponseDto dto = new RoleResponseDto(idRol, "TEST", "Test role");
        
        assertEquals(idRol, dto.idRol());
    }

    @Test
    void nombre_shouldReturnCorrectValue() {
        String nombre = "SUPER_ADMIN";
        RoleResponseDto dto = new RoleResponseDto(1, nombre, "Super administrator");
        
        assertEquals(nombre, dto.nombre());
    }

    @Test
    void descripcion_shouldReturnCorrectValue() {
        String descripcion = "Usuario con permisos completos del sistema";
        RoleResponseDto dto = new RoleResponseDto(1, "ADMIN", descripcion);
        
        assertEquals(descripcion, dto.descripcion());
    }

    @Test
    void constructor_withSpecialCharacters_shouldAccept() {
        Integer idRol = 1;
        String nombreEspecial = "ROL_ESPECIAL";
        String descripcionEspecial = "Rol con caracteres especiales: áéíóú, ñÑ, @#$%&*()";
        
        RoleResponseDto dto = new RoleResponseDto(idRol, nombreEspecial, descripcionEspecial);
        
        assertEquals(idRol, dto.idRol());
        assertEquals(nombreEspecial, dto.nombre());
        assertEquals(descripcionEspecial, dto.descripcion());
    }

    @Test
    void constructor_withLongValues_shouldAccept() {
        Integer idRol = Integer.MAX_VALUE;
        String nombreLargo = "ROL_CON_NOMBRE_MUY_LARGO_PARA_TESTING_DE_LIMITES";
        String descripcionLarga = "Esta es una descripción muy larga que puede contener múltiples " +
                "líneas de texto y caracteres especiales para probar el comportamiento del DTO " +
                "cuando recibe valores extensos en respuestas de la API.";
        
        RoleResponseDto dto = new RoleResponseDto(idRol, nombreLargo, descripcionLarga);
        
        assertEquals(idRol, dto.idRol());
        assertEquals(nombreLargo, dto.nombre());
        assertEquals(descripcionLarga, dto.descripcion());
    }

    @Test
    void constructor_withEmptyStrings_shouldAccept() {
        Integer idRol = 0;
        String emptyNombre = "";
        String emptyDescripcion = "";
        
        RoleResponseDto dto = new RoleResponseDto(idRol, emptyNombre, emptyDescripcion);
        
        assertEquals(idRol, dto.idRol());
        assertEquals(emptyNombre, dto.nombre());
        assertEquals(emptyDescripcion, dto.descripcion());
    }

    @Test
    void equals_withNullValues_shouldWork() {
        RoleResponseDto dto1 = new RoleResponseDto(null, null, null);
        RoleResponseDto dto2 = new RoleResponseDto(null, null, null);
        RoleResponseDto dto3 = new RoleResponseDto(1, null, null);
        
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    void hashCode_withNullValues_shouldNotThrow() {
        RoleResponseDto dto = new RoleResponseDto(null, null, null);
        
        assertDoesNotThrow(dto::hashCode);
    }

    @Test
    void toString_withNullValues_shouldNotThrow() {
        RoleResponseDto dto = new RoleResponseDto(null, null, null);
        
        assertDoesNotThrow(() -> {
            String result = dto.toString();
            assertTrue(result.contains("RoleResponseDto"));
        });
    }

    @Test
    void constructor_withNegativeId_shouldAccept() {
        Integer negativeId = -1;
        RoleResponseDto dto = new RoleResponseDto(negativeId, "INVALID", "Invalid role");
        
        assertEquals(negativeId, dto.idRol());
    }

    @Test
    void constructor_withWhitespaceValues_shouldAccept() {
        Integer idRol = 1;
        String nombreConEspacios = "  ADMIN  ";
        String descripcionConEspacios = "  Administrador del sistema  ";
        
        RoleResponseDto dto = new RoleResponseDto(idRol, nombreConEspacios, descripcionConEspacios);
        
        assertEquals(idRol, dto.idRol());
        assertEquals(nombreConEspacios, dto.nombre());
        assertEquals(descripcionConEspacios, dto.descripcion());
    }
}
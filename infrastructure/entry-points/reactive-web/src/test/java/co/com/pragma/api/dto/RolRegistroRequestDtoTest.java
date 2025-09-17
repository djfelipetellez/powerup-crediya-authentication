package co.com.pragma.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RolRegistroRequestDtoTest {

    @Test
    void constructor_withValidData_shouldCreateInstance() {
        String nombre = "ADMIN";
        String descripcion = "Administrador del sistema";
        
        RolRegistroRequestDto dto = new RolRegistroRequestDto(nombre, descripcion);
        
        assertEquals(nombre, dto.nombre());
        assertEquals(descripcion, dto.descripcion());
    }

    @Test
    void constructor_withNullValues_shouldAcceptNull() {
        RolRegistroRequestDto dto = new RolRegistroRequestDto(null, null);
        
        assertNull(dto.nombre());
        assertNull(dto.descripcion());
    }

    @Test
    void constructor_withEmptyValues_shouldAcceptEmpty() {
        String emptyNombre = "";
        String emptyDescripcion = "";
        
        RolRegistroRequestDto dto = new RolRegistroRequestDto(emptyNombre, emptyDescripcion);
        
        assertEquals(emptyNombre, dto.nombre());
        assertEquals(emptyDescripcion, dto.descripcion());
    }

    @Test
    void equals_withSameData_shouldBeEqual() {
        String nombre = "ASESOR";
        String descripcion = "Asesor del banco";
        
        RolRegistroRequestDto dto1 = new RolRegistroRequestDto(nombre, descripcion);
        RolRegistroRequestDto dto2 = new RolRegistroRequestDto(nombre, descripcion);
        
        assertEquals(dto1, dto2);
    }

    @Test
    void equals_withDifferentNombre_shouldNotBeEqual() {
        RolRegistroRequestDto dto1 = new RolRegistroRequestDto("ADMIN", "Administrador");
        RolRegistroRequestDto dto2 = new RolRegistroRequestDto("ASESOR", "Administrador");
        
        assertNotEquals(dto1, dto2);
    }

    @Test
    void equals_withDifferentDescripcion_shouldNotBeEqual() {
        RolRegistroRequestDto dto1 = new RolRegistroRequestDto("ADMIN", "Administrador del sistema");
        RolRegistroRequestDto dto2 = new RolRegistroRequestDto("ADMIN", "Administrador de la aplicación");
        
        assertNotEquals(dto1, dto2);
    }

    @Test
    void hashCode_withSameData_shouldBeSame() {
        String nombre = "CLIENTE";
        String descripcion = "Cliente del banco";
        
        RolRegistroRequestDto dto1 = new RolRegistroRequestDto(nombre, descripcion);
        RolRegistroRequestDto dto2 = new RolRegistroRequestDto(nombre, descripcion);
        
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_shouldContainBothFields() {
        String nombre = "ADMIN";
        String descripcion = "Administrador del sistema";
        
        RolRegistroRequestDto dto = new RolRegistroRequestDto(nombre, descripcion);
        String result = dto.toString();
        
        assertTrue(result.contains(nombre));
        assertTrue(result.contains(descripcion));
        assertTrue(result.contains("RolRegistroRequestDto"));
    }

    @Test
    void nombre_shouldReturnCorrectValue() {
        String nombre = "SUPER_ADMIN";
        RolRegistroRequestDto dto = new RolRegistroRequestDto(nombre, "Súper administrador");
        
        assertEquals(nombre, dto.nombre());
    }

    @Test
    void descripcion_shouldReturnCorrectValue() {
        String descripcion = "Usuario con permisos completos";
        RolRegistroRequestDto dto = new RolRegistroRequestDto("ADMIN", descripcion);
        
        assertEquals(descripcion, dto.descripcion());
    }

    @Test
    void constructor_withSpecialCharacters_shouldAccept() {
        String nombreEspecial = "ROL_ESPECIAL";
        String descripcionEspecial = "Rol con caracteres especiales: áéíóú, ñÑ, @#$%";
        
        RolRegistroRequestDto dto = new RolRegistroRequestDto(nombreEspecial, descripcionEspecial);
        
        assertEquals(nombreEspecial, dto.nombre());
        assertEquals(descripcionEspecial, dto.descripcion());
    }

    @Test
    void constructor_withLongValues_shouldAccept() {
        String nombreLargo = "ROL_CON_NOMBRE_MUY_LARGO_PARA_TESTING";
        String descripcionLarga = "Esta es una descripción muy larga que puede contener múltiples " +
                "líneas de texto y caracteres especiales para probar el comportamiento del DTO " +
                "cuando recibe valores extensos que podrían ser usados en escenarios reales.";
        
        RolRegistroRequestDto dto = new RolRegistroRequestDto(nombreLargo, descripcionLarga);
        
        assertEquals(nombreLargo, dto.nombre());
        assertEquals(descripcionLarga, dto.descripcion());
    }

    @Test
    void constructor_withRoleTypes_shouldAccept() {
        String[] roleNames = {"ADMIN", "ASESOR", "CLIENTE", "MODERADOR", "SUPERVISOR"};
        String[] descriptions = {
            "Administrador del sistema",
            "Asesor bancario",
            "Cliente del banco",
            "Moderador de contenido",
            "Supervisor de operaciones"
        };
        
        for (int i = 0; i < roleNames.length; i++) {
            RolRegistroRequestDto dto = new RolRegistroRequestDto(roleNames[i], descriptions[i]);
            
            assertEquals(roleNames[i], dto.nombre());
            assertEquals(descriptions[i], dto.descripcion());
        }
    }

    @Test
    void equals_withNullValues_shouldWork() {
        RolRegistroRequestDto dto1 = new RolRegistroRequestDto(null, null);
        RolRegistroRequestDto dto2 = new RolRegistroRequestDto(null, null);
        RolRegistroRequestDto dto3 = new RolRegistroRequestDto("ADMIN", null);
        
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    void hashCode_withNullValues_shouldNotThrow() {
        RolRegistroRequestDto dto = new RolRegistroRequestDto(null, null);
        
        assertDoesNotThrow(dto::hashCode);
    }

    @Test
    void toString_withNullValues_shouldNotThrow() {
        RolRegistroRequestDto dto = new RolRegistroRequestDto(null, null);
        
        assertDoesNotThrow(() -> {
            String result = dto.toString();
            assertTrue(result.contains("RolRegistroRequestDto"));
        });
    }

    @Test
    void constructor_withWhitespaceValues_shouldAccept() {
        String nombreConEspacios = "  ADMIN  ";
        String descripcionConEspacios = "  Administrador del sistema  ";
        
        RolRegistroRequestDto dto = new RolRegistroRequestDto(nombreConEspacios, descripcionConEspacios);
        
        assertEquals(nombreConEspacios, dto.nombre());
        assertEquals(descripcionConEspacios, dto.descripcion());
    }
}
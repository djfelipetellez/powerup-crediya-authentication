package co.com.pragma.r2dbc.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioEntityTest {

    @Test
    void noArgsConstructor_shouldCreateEmptyInstance() {
        UsuarioEntity entity = new UsuarioEntity();
        
        assertNull(entity.getIdUsuario());
        assertNull(entity.getNombre());
        assertNull(entity.getApellido());
        assertNull(entity.getEmail());
        assertNull(entity.getDocumentoIdentidad());
        assertNull(entity.getTelefono());
        assertNull(entity.getSalarioBase());
        assertNull(entity.getIdRol());
    }

    @Test
    void allArgsConstructor_shouldCreateInstanceWithAllFields() {
        Integer idUsuario = 1;
        String nombre = "Juan Carlos";
        String apellido = "Pérez González";
        String email = "juan.perez@example.com";
        String documentoIdentidad = "12345678";
        String telefono = "3001234567";
        BigDecimal salarioBase = new BigDecimal("2500000.00");
        Integer idRol = 2;
        
        UsuarioEntity entity = new UsuarioEntity(
                idUsuario, nombre, apellido, email, documentoIdentidad, telefono, salarioBase, idRol
        );
        
        assertEquals(idUsuario, entity.getIdUsuario());
        assertEquals(nombre, entity.getNombre());
        assertEquals(apellido, entity.getApellido());
        assertEquals(email, entity.getEmail());
        assertEquals(documentoIdentidad, entity.getDocumentoIdentidad());
        assertEquals(telefono, entity.getTelefono());
        assertEquals(salarioBase, entity.getSalarioBase());
        assertEquals(idRol, entity.getIdRol());
    }

    @Test
    void builder_shouldCreateInstanceWithAllFields() {
        Integer idUsuario = 2;
        String nombre = "María";
        String apellido = "García López";
        String email = "maria.garcia@company.com";
        String documentoIdentidad = "87654321";
        String telefono = "3109876543";
        BigDecimal salarioBase = new BigDecimal("1800000.50");
        Integer idRol = 3;
        
        UsuarioEntity entity = UsuarioEntity.builder()
                .idUsuario(idUsuario)
                .nombre(nombre)
                .apellido(apellido)
                .email(email)
                .documentoIdentidad(documentoIdentidad)
                .telefono(telefono)
                .salarioBase(salarioBase)
                .idRol(idRol)
                .build();
        
        assertEquals(idUsuario, entity.getIdUsuario());
        assertEquals(nombre, entity.getNombre());
        assertEquals(apellido, entity.getApellido());
        assertEquals(email, entity.getEmail());
        assertEquals(documentoIdentidad, entity.getDocumentoIdentidad());
        assertEquals(telefono, entity.getTelefono());
        assertEquals(salarioBase, entity.getSalarioBase());
        assertEquals(idRol, entity.getIdRol());
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        String nombre = "Carlos";
        String email = "carlos@test.com";
        Integer idRol = 1;
        
        UsuarioEntity entity = UsuarioEntity.builder()
                .nombre(nombre)
                .email(email)
                .idRol(idRol)
                .build();
        
        assertNull(entity.getIdUsuario());
        assertEquals(nombre, entity.getNombre());
        assertNull(entity.getApellido());
        assertEquals(email, entity.getEmail());
        assertNull(entity.getDocumentoIdentidad());
        assertNull(entity.getTelefono());
        assertNull(entity.getSalarioBase());
        assertEquals(idRol, entity.getIdRol());
    }

    @Test
    void setters_shouldUpdateAllFields() {
        UsuarioEntity entity = new UsuarioEntity();
        Integer idUsuario = 5;
        String nombre = "Pedro";
        String apellido = "Martínez Silva";
        String email = "pedro.martinez@email.com";
        String documentoIdentidad = "55667788";
        String telefono = "3205556677";
        BigDecimal salarioBase = new BigDecimal("3000000.75");
        Integer idRol = 1;
        
        entity.setIdUsuario(idUsuario);
        entity.setNombre(nombre);
        entity.setApellido(apellido);
        entity.setEmail(email);
        entity.setDocumentoIdentidad(documentoIdentidad);
        entity.setTelefono(telefono);
        entity.setSalarioBase(salarioBase);
        entity.setIdRol(idRol);
        
        assertEquals(idUsuario, entity.getIdUsuario());
        assertEquals(nombre, entity.getNombre());
        assertEquals(apellido, entity.getApellido());
        assertEquals(email, entity.getEmail());
        assertEquals(documentoIdentidad, entity.getDocumentoIdentidad());
        assertEquals(telefono, entity.getTelefono());
        assertEquals(salarioBase, entity.getSalarioBase());
        assertEquals(idRol, entity.getIdRol());
    }

    @Test
    void setters_withNullValues_shouldAcceptNull() {
        UsuarioEntity entity = UsuarioEntity.builder()
                .idUsuario(1)
                .nombre("Test")
                .email("test@example.com")
                .idRol(1)
                .build();
        
        entity.setIdUsuario(null);
        entity.setNombre(null);
        entity.setApellido(null);
        entity.setEmail(null);
        entity.setDocumentoIdentidad(null);
        entity.setTelefono(null);
        entity.setSalarioBase(null);
        entity.setIdRol(null);
        
        assertNull(entity.getIdUsuario());
        assertNull(entity.getNombre());
        assertNull(entity.getApellido());
        assertNull(entity.getEmail());
        assertNull(entity.getDocumentoIdentidad());
        assertNull(entity.getTelefono());
        assertNull(entity.getSalarioBase());
        assertNull(entity.getIdRol());
    }

    @Test
    void salarioBase_withDifferentPrecision_shouldMaintainValue() {
        BigDecimal salarioConDecimales = new BigDecimal("1234567.89");
        BigDecimal salarioSinDecimales = new BigDecimal("5000000");
        
        UsuarioEntity entity1 = UsuarioEntity.builder()
                .salarioBase(salarioConDecimales)
                .build();
        
        UsuarioEntity entity2 = UsuarioEntity.builder()
                .salarioBase(salarioSinDecimales)
                .build();
        
        assertEquals(salarioConDecimales, entity1.getSalarioBase());
        assertEquals(salarioSinDecimales, entity2.getSalarioBase());
    }

    @Test
    void nombre_withSpecialCharacters_shouldBeAccepted() {
        String nombreEspecial = "José María";
        String apellidoEspecial = "González-Rodríguez";
        
        UsuarioEntity entity = UsuarioEntity.builder()
                .nombre(nombreEspecial)
                .apellido(apellidoEspecial)
                .build();
        
        assertEquals(nombreEspecial, entity.getNombre());
        assertEquals(apellidoEspecial, entity.getApellido());
    }

    @Test
    void email_withSpecialCharacters_shouldBeAccepted() {
        String emailEspecial = "user+test@sub-domain.example.com";
        
        UsuarioEntity entity = UsuarioEntity.builder()
                .email(emailEspecial)
                .build();
        
        assertEquals(emailEspecial, entity.getEmail());
    }

    @Test
    void documentoIdentidad_withDifferentFormats_shouldBeAccepted() {
        String documento1 = "12345678";
        String documento2 = "CC-12345678";
        String documento3 = "1.234.567-8";
        
        UsuarioEntity entity1 = UsuarioEntity.builder()
                .documentoIdentidad(documento1)
                .build();
        
        UsuarioEntity entity2 = UsuarioEntity.builder()
                .documentoIdentidad(documento2)
                .build();
        
        UsuarioEntity entity3 = UsuarioEntity.builder()
                .documentoIdentidad(documento3)
                .build();
        
        assertEquals(documento1, entity1.getDocumentoIdentidad());
        assertEquals(documento2, entity2.getDocumentoIdentidad());
        assertEquals(documento3, entity3.getDocumentoIdentidad());
    }

    @Test
    void telefono_withDifferentFormats_shouldBeAccepted() {
        String telefono1 = "3001234567";
        String telefono2 = "+57-300-123-4567";
        String telefono3 = "(300) 123-4567";
        
        UsuarioEntity entity1 = UsuarioEntity.builder()
                .telefono(telefono1)
                .build();
        
        UsuarioEntity entity2 = UsuarioEntity.builder()
                .telefono(telefono2)
                .build();
        
        UsuarioEntity entity3 = UsuarioEntity.builder()
                .telefono(telefono3)
                .build();
        
        assertEquals(telefono1, entity1.getTelefono());
        assertEquals(telefono2, entity2.getTelefono());
        assertEquals(telefono3, entity3.getTelefono());
    }

    @Test
    void idRol_withDifferentValues_shouldBeAccepted() {
        Integer idRolAdmin = 1;
        Integer idRolAsesor = 2;
        Integer idRolCliente = 3;
        
        UsuarioEntity entityAdmin = UsuarioEntity.builder()
                .idRol(idRolAdmin)
                .build();
        
        UsuarioEntity entityAsesor = UsuarioEntity.builder()
                .idRol(idRolAsesor)
                .build();
        
        UsuarioEntity entityCliente = UsuarioEntity.builder()
                .idRol(idRolCliente)
                .build();
        
        assertEquals(idRolAdmin, entityAdmin.getIdRol());
        assertEquals(idRolAsesor, entityAsesor.getIdRol());
        assertEquals(idRolCliente, entityCliente.getIdRol());
    }

    @Test
    void salarioBase_withLargeValues_shouldBeAccepted() {
        BigDecimal salarioGrande = new BigDecimal("999999999.99");
        
        UsuarioEntity entity = UsuarioEntity.builder()
                .salarioBase(salarioGrande)
                .build();
        
        assertEquals(salarioGrande, entity.getSalarioBase());
    }

    @Test
    void salarioBase_withZeroValue_shouldBeAccepted() {
        BigDecimal salarioCero = BigDecimal.ZERO;
        
        UsuarioEntity entity = UsuarioEntity.builder()
                .salarioBase(salarioCero)
                .build();
        
        assertEquals(salarioCero, entity.getSalarioBase());
    }

    @Test
    void builder_chainedCalls_shouldWork() {
        UsuarioEntity entity = UsuarioEntity.builder()
                .idUsuario(1)
                .nombre("Test")
                .apellido("User")
                .email("test@example.com")
                .documentoIdentidad("12345678")
                .telefono("3001234567")
                .salarioBase(new BigDecimal("2000000"))
                .idRol(3)
                .build();
        
        assertNotNull(entity);
        assertEquals(1, entity.getIdUsuario());
        assertEquals("Test", entity.getNombre());
        assertEquals("User", entity.getApellido());
        assertEquals("test@example.com", entity.getEmail());
        assertEquals("12345678", entity.getDocumentoIdentidad());
        assertEquals("3001234567", entity.getTelefono());
        assertEquals(new BigDecimal("2000000"), entity.getSalarioBase());
        assertEquals(3, entity.getIdRol());
    }

    @Test
    void idUsuario_withLargeValue_shouldBeAccepted() {
        Integer largeId = Integer.MAX_VALUE;
        
        UsuarioEntity entity = UsuarioEntity.builder()
                .idUsuario(largeId)
                .build();
        
        assertEquals(largeId, entity.getIdUsuario());
    }

    @Test
    void campos_withEmptyStrings_shouldBeAccepted() {
        UsuarioEntity entity = UsuarioEntity.builder()
                .nombre("")
                .apellido("")
                .email("")
                .documentoIdentidad("")
                .telefono("")
                .build();
        
        assertEquals("", entity.getNombre());
        assertEquals("", entity.getApellido());
        assertEquals("", entity.getEmail());
        assertEquals("", entity.getDocumentoIdentidad());
        assertEquals("", entity.getTelefono());
    }
}
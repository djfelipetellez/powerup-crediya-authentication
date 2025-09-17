package co.com.pragma.r2dbc.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RolEntityTest {

    @Test
    void noArgsConstructor_shouldCreateEmptyInstance() {
        RolEntity entity = new RolEntity();

        assertNull(entity.getIdRol());
        assertNull(entity.getNombre());
        assertNull(entity.getDescripcion());
    }

    @Test
    void allArgsConstructor_shouldCreateInstanceWithAllFields() {
        Integer idRol = 1;
        String nombre = "ADMIN";
        String descripcion = "Administrador del sistema";

        RolEntity entity = new RolEntity(idRol, nombre, descripcion);

        assertEquals(idRol, entity.getIdRol());
        assertEquals(nombre, entity.getNombre());
        assertEquals(descripcion, entity.getDescripcion());
    }

    @Test
    void builder_shouldCreateInstanceWithAllFields() {
        Integer idRol = 2;
        String nombre = "ASESOR";
        String descripcion = "Asesor del banco";

        RolEntity entity = RolEntity.builder()
                .idRol(idRol)
                .nombre(nombre)
                .descripcion(descripcion)
                .build();

        assertEquals(idRol, entity.getIdRol());
        assertEquals(nombre, entity.getNombre());
        assertEquals(descripcion, entity.getDescripcion());
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        String nombre = "CLIENTE";

        RolEntity entity = RolEntity.builder()
                .nombre(nombre)
                .build();

        assertNull(entity.getIdRol());
        assertEquals(nombre, entity.getNombre());
        assertNull(entity.getDescripcion());
    }

    @Test
    void setters_shouldUpdateAllFields() {
        RolEntity entity = new RolEntity();
        Integer idRol = 3;
        String nombre = "CLIENTE";
        String descripcion = "Cliente del banco";

        entity.setIdRol(idRol);
        entity.setNombre(nombre);
        entity.setDescripcion(descripcion);

        assertEquals(idRol, entity.getIdRol());
        assertEquals(nombre, entity.getNombre());
        assertEquals(descripcion, entity.getDescripcion());
    }

    @Test
    void setters_withNullValues_shouldAcceptNull() {
        RolEntity entity = RolEntity.builder()
                .idRol(1)
                .nombre("TEST")
                .descripcion("Test role")
                .build();

        entity.setIdRol(null);
        entity.setNombre(null);
        entity.setDescripcion(null);

        assertNull(entity.getIdRol());
        assertNull(entity.getNombre());
        assertNull(entity.getDescripcion());
    }

    @Test
    void nombre_withDifferentRoleTypes_shouldBeAccepted() {
        RolEntity adminEntity = RolEntity.builder()
                .nombre("ADMIN")
                .build();

        RolEntity asesorEntity = RolEntity.builder()
                .nombre("ASESOR")
                .build();

        RolEntity clienteEntity = RolEntity.builder()
                .nombre("CLIENTE")
                .build();

        assertEquals("ADMIN", adminEntity.getNombre());
        assertEquals("ASESOR", asesorEntity.getNombre());
        assertEquals("CLIENTE", clienteEntity.getNombre());
    }

    @Test
    void descripcion_withLongText_shouldBeAccepted() {
        String descripcionLarga = "Esta es una descripción muy larga que podría contener " +
                "múltiples líneas de texto y caracteres especiales como áéíóú, ñÑ, " +
                "y símbolos como @#$%&*()_+-=[]{}|;':\",./<>?";

        RolEntity entity = RolEntity.builder()
                .descripcion(descripcionLarga)
                .build();

        assertEquals(descripcionLarga, entity.getDescripcion());
    }

    @Test
    void nombre_withSpecialCharacters_shouldBeAccepted() {
        String nombreEspecial = "ADMIN_SUPER";

        RolEntity entity = RolEntity.builder()
                .nombre(nombreEspecial)
                .build();

        assertEquals(nombreEspecial, entity.getNombre());
    }

    @Test
    void idRol_withDifferentValues_shouldBeAccepted() {
        Integer idRolUno = 1;
        Integer idRolDos = 2;
        Integer idRolTres = 3;

        RolEntity entity1 = RolEntity.builder()
                .idRol(idRolUno)
                .build();

        RolEntity entity2 = RolEntity.builder()
                .idRol(idRolDos)
                .build();

        RolEntity entity3 = RolEntity.builder()
                .idRol(idRolTres)
                .build();

        assertEquals(idRolUno, entity1.getIdRol());
        assertEquals(idRolDos, entity2.getIdRol());
        assertEquals(idRolTres, entity3.getIdRol());
    }

    @Test
    void builder_chainedCalls_shouldWork() {
        RolEntity entity = RolEntity.builder()
                .idRol(1)
                .nombre("ADMIN")
                .descripcion("Administrador del sistema")
                .build();

        assertNotNull(entity);
        assertEquals(1, entity.getIdRol());
        assertEquals("ADMIN", entity.getNombre());
        assertEquals("Administrador del sistema", entity.getDescripcion());
    }

    @Test
    void campos_withEmptyStrings_shouldBeAccepted() {
        RolEntity entity = RolEntity.builder()
                .nombre("")
                .descripcion("")
                .build();

        assertEquals("", entity.getNombre());
        assertEquals("", entity.getDescripcion());
    }

    @Test
    void idRol_withLargeValue_shouldBeAccepted() {
        Integer largeId = Integer.MAX_VALUE;

        RolEntity entity = RolEntity.builder()
                .idRol(largeId)
                .build();

        assertEquals(largeId, entity.getIdRol());
    }

    @Test
    void idRol_withZeroValue_shouldBeAccepted() {
        Integer zeroId = 0;

        RolEntity entity = RolEntity.builder()
                .idRol(zeroId)
                .build();

        assertEquals(zeroId, entity.getIdRol());
    }

    @Test
    void descripcion_withSpecialCharacters_shouldBeAccepted() {
        String descripcionEspecial = "Rol para administradores: @usuarios & $sistemas (100% confiables)";

        RolEntity entity = RolEntity.builder()
                .descripcion(descripcionEspecial)
                .build();

        assertEquals(descripcionEspecial, entity.getDescripcion());
    }

    @Test
    void todosLosCampos_conValoresReales_shouldWork() {
        RolEntity adminEntity = new RolEntity(1, "ADMIN", "Administrador del sistema con permisos completos");
        RolEntity asesorEntity = new RolEntity(2, "ASESOR", "Asesor bancario para atención al cliente");
        RolEntity clienteEntity = new RolEntity(3, "CLIENTE", "Cliente del banco que solicita préstamos");

        // Verificar ADMIN
        assertEquals(1, adminEntity.getIdRol());
        assertEquals("ADMIN", adminEntity.getNombre());
        assertEquals("Administrador del sistema con permisos completos", adminEntity.getDescripcion());

        // Verificar ASESOR
        assertEquals(2, asesorEntity.getIdRol());
        assertEquals("ASESOR", asesorEntity.getNombre());
        assertEquals("Asesor bancario para atención al cliente", asesorEntity.getDescripcion());

        // Verificar CLIENTE
        assertEquals(3, clienteEntity.getIdRol());
        assertEquals("CLIENTE", clienteEntity.getNombre());
        assertEquals("Cliente del banco que solicita préstamos", clienteEntity.getDescripcion());
    }
}
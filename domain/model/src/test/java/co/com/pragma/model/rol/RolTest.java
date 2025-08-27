package co.com.pragma.model.rol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RolTest {

    @Test
    void testNoArgsConstructor() {
        Rol rol = new Rol();
        assertNull(rol.getIdRol());
        assertNull(rol.getNombre());
        assertNull(rol.getDescripcion());
    }

    @Test
    void testAllArgsConstructor() {
        Rol rol = new Rol(1, "Admin", "Administrador del sistema");
        assertEquals(1, rol.getIdRol());
        assertEquals("Admin", rol.getNombre());
        assertEquals("Administrador del sistema", rol.getDescripcion());
    }

    @Test
    void testBuilder() {
        Rol rol = Rol.builder()
                .idRol(1)
                .nombre("Admin")
                .descripcion("Administrador del sistema")
                .build();
        assertEquals(1, rol.getIdRol());
        assertEquals("Admin", rol.getNombre());
        assertEquals("Administrador del sistema", rol.getDescripcion());
    }

    @Test
    void testToBuilder() {
        Rol rol1 = Rol.builder()
                .idRol(1)
                .nombre("Admin")
                .descripcion("Administrador del sistema")
                .build();
        Rol rol2 = rol1.toBuilder().nombre("Super Admin").build();
        assertEquals(1, rol2.getIdRol());
        assertEquals("Super Admin", rol2.getNombre());
        assertEquals("Administrador del sistema", rol2.getDescripcion());
    }

    @Test
    void testGetters() {
        Rol rol = new Rol(1, "Admin", "Administrador del sistema");
        assertEquals(1, rol.getIdRol());
        assertEquals("Admin", rol.getNombre());
        assertEquals("Administrador del sistema", rol.getDescripcion());
    }
}

package co.com.pragma.model.usuario;

import co.com.pragma.model.rol.Rol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UsuarioTest {

    @Test
    void testNoArgsConstructor() {
        Usuario usuario = new Usuario();
        assertNull(usuario.getIdUsuario());
        assertNull(usuario.getNombre());
        assertNull(usuario.getApellido());
        assertNull(usuario.getEmail());
        assertNull(usuario.getDocumentoIdentidad());
        assertNull(usuario.getTelefono());
        assertNull(usuario.getSalarioBase());
        assertNull(usuario.getRol());
    }

    @Test
    void testAllArgsConstructor() {
        Rol rol = new Rol(1, "Admin", "Administrador");
        Usuario usuario = new Usuario(1, "Juan", "Perez", "juan.perez@example.com", "123456789", "3001234567", 50000.0, rol);
        assertEquals(1, usuario.getIdUsuario());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Perez", usuario.getApellido());
        assertEquals("juan.perez@example.com", usuario.getEmail());
        assertEquals("123456789", usuario.getDocumentoIdentidad());
        assertEquals("3001234567", usuario.getTelefono());
        assertEquals(50000.0, usuario.getSalarioBase());
        assertEquals(rol, usuario.getRol());
    }

    @Test
    void testBuilder() {
        Rol rol = new Rol(1, "Admin", "Administrador");
        Usuario usuario = Usuario.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan.perez@example.com")
                .documentoIdentidad("123456789")
                .telefono("3001234567")
                .salarioBase(50000.0)
                .rol(rol)
                .build();
        assertEquals(1, usuario.getIdUsuario());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Perez", usuario.getApellido());
        assertEquals("juan.perez@example.com", usuario.getEmail());
        assertEquals("123456789", usuario.getDocumentoIdentidad());
        assertEquals("3001234567", usuario.getTelefono());
        assertEquals(50000.0, usuario.getSalarioBase());
        assertEquals(rol, usuario.getRol());
    }

    @Test
    void testToBuilder() {
        Rol rol = new Rol(1, "Admin", "Administrador");
        Usuario usuario1 = Usuario.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan.perez@example.com")
                .documentoIdentidad("123456789")
                .telefono("3001234567")
                .salarioBase(50000.0)
                .rol(rol)
                .build();
        Usuario usuario2 = usuario1.toBuilder().nombre("John").build();
        assertEquals(1, usuario2.getIdUsuario());
        assertEquals("John", usuario2.getNombre());
        assertEquals("Perez", usuario2.getApellido());
        assertEquals("juan.perez@example.com", usuario2.getEmail());
        assertEquals("123456789", usuario2.getDocumentoIdentidad());
        assertEquals("3001234567", usuario2.getTelefono());
        assertEquals(50000.0, usuario2.getSalarioBase());
        assertEquals(rol, usuario2.getRol());
    }

    @Test
    void testGettersAndSetters() {
        Usuario usuario = new Usuario();
        Rol rol = new Rol(1, "Admin", "Administrador");
        usuario.setIdUsuario(1);
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setEmail("juan.perez@example.com");
        usuario.setDocumentoIdentidad("123456789");
        usuario.setTelefono("3001234567");
        usuario.setSalarioBase(50000.0);
        usuario.setRol(rol);

        assertEquals(1, usuario.getIdUsuario());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Perez", usuario.getApellido());
        assertEquals("juan.perez@example.com", usuario.getEmail());
        assertEquals("123456789", usuario.getDocumentoIdentidad());
        assertEquals("3001234567", usuario.getTelefono());
        assertEquals(50000.0, usuario.getSalarioBase());
        assertEquals(rol, usuario.getRol());
    }
}

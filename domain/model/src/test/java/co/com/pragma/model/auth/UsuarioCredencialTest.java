package co.com.pragma.model.auth;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioCredencialTest {

    @Test
    void noArgsConstructor_shouldCreateInstance() {
        UsuarioCredencial usuarioCredencial = new UsuarioCredencial();

        assertNotNull(usuarioCredencial);
        assertNull(usuarioCredencial.getId());
        assertNull(usuarioCredencial.getEmail());
        assertNull(usuarioCredencial.getPassword());
        assertNull(usuarioCredencial.getIdUsuario());
        assertNull(usuarioCredencial.getCreatedAt());
        assertNull(usuarioCredencial.getLastLoginAt());
        assertFalse(usuarioCredencial.isActive());
    }

    @Test
    void allArgsConstructor_shouldCreateInstanceWithAllFields() {
        Integer id = 1;
        String email = "test@example.com";
        String password = "password123";
        Integer idUsuario = 100;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime lastLoginAt = LocalDateTime.now().minusHours(1);
        boolean active = true;

        UsuarioCredencial usuarioCredencial = new UsuarioCredencial(id, email, password, idUsuario, createdAt, lastLoginAt, active);

        assertEquals(id, usuarioCredencial.getId());
        assertEquals(email, usuarioCredencial.getEmail());
        assertEquals(password, usuarioCredencial.getPassword());
        assertEquals(idUsuario, usuarioCredencial.getIdUsuario());
        assertEquals(createdAt, usuarioCredencial.getCreatedAt());
        assertEquals(lastLoginAt, usuarioCredencial.getLastLoginAt());
        assertTrue(usuarioCredencial.isActive());
    }

    @Test
    void builder_shouldCreateInstanceWithAllFields() {
        Integer id = 1;
        String email = "test@example.com";
        String password = "password123";
        Integer idUsuario = 100;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime lastLoginAt = LocalDateTime.now().minusHours(1);
        boolean active = true;

        UsuarioCredencial usuarioCredencial = UsuarioCredencial.builder()
                .id(id)
                .email(email)
                .password(password)
                .idUsuario(idUsuario)
                .createdAt(createdAt)
                .lastLoginAt(lastLoginAt)
                .active(active)
                .build();

        assertEquals(id, usuarioCredencial.getId());
        assertEquals(email, usuarioCredencial.getEmail());
        assertEquals(password, usuarioCredencial.getPassword());
        assertEquals(idUsuario, usuarioCredencial.getIdUsuario());
        assertEquals(createdAt, usuarioCredencial.getCreatedAt());
        assertEquals(lastLoginAt, usuarioCredencial.getLastLoginAt());
        assertTrue(usuarioCredencial.isActive());
    }

    @Test
    void setters_shouldUpdateFields() {
        UsuarioCredencial usuarioCredencial = new UsuarioCredencial();
        Integer id = 1;
        String email = "test@example.com";
        String password = "password123";
        Integer idUsuario = 100;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime lastLoginAt = LocalDateTime.now().minusHours(1);
        boolean active = true;

        usuarioCredencial.setId(id);
        usuarioCredencial.setEmail(email);
        usuarioCredencial.setPassword(password);
        usuarioCredencial.setIdUsuario(idUsuario);
        usuarioCredencial.setCreatedAt(createdAt);
        usuarioCredencial.setLastLoginAt(lastLoginAt);
        usuarioCredencial.setActive(active);

        assertEquals(id, usuarioCredencial.getId());
        assertEquals(email, usuarioCredencial.getEmail());
        assertEquals(password, usuarioCredencial.getPassword());
        assertEquals(idUsuario, usuarioCredencial.getIdUsuario());
        assertEquals(createdAt, usuarioCredencial.getCreatedAt());
        assertEquals(lastLoginAt, usuarioCredencial.getLastLoginAt());
        assertTrue(usuarioCredencial.isActive());
    }

    @Test
    void toBuilder_shouldCreateBuilderFromExistingInstance() {
        UsuarioCredencial original = UsuarioCredencial.builder()
                .id(1)
                .email("test@example.com")
                .password("password123")
                .idUsuario(100)
                .active(true)
                .build();

        UsuarioCredencial modified = original.toBuilder()
                .email("newemail@example.com")
                .active(false)
                .build();

        assertEquals(original.getId(), modified.getId());
        assertEquals("newemail@example.com", modified.getEmail());
        assertEquals(original.getPassword(), modified.getPassword());
        assertEquals(original.getIdUsuario(), modified.getIdUsuario());
        assertEquals(original.getCreatedAt(), modified.getCreatedAt());
        assertEquals(original.getLastLoginAt(), modified.getLastLoginAt());
        assertFalse(modified.isActive());
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        String email = "test@example.com";
        Integer idUsuario = 100;

        UsuarioCredencial usuarioCredencial = UsuarioCredencial.builder()
                .email(email)
                .idUsuario(idUsuario)
                .active(true)
                .build();

        assertNull(usuarioCredencial.getId());
        assertEquals(email, usuarioCredencial.getEmail());
        assertNull(usuarioCredencial.getPassword());
        assertEquals(idUsuario, usuarioCredencial.getIdUsuario());
        assertNull(usuarioCredencial.getCreatedAt());
        assertNull(usuarioCredencial.getLastLoginAt());
        assertTrue(usuarioCredencial.isActive());
    }

    @Test
    void isActive_defaultValue_shouldBeFalse() {
        UsuarioCredencial usuarioCredencial = new UsuarioCredencial();

        assertFalse(usuarioCredencial.isActive());
    }

    @Test
    void setActive_shouldUpdateActiveStatus() {
        UsuarioCredencial usuarioCredencial = new UsuarioCredencial();

        usuarioCredencial.setActive(true);
        assertTrue(usuarioCredencial.isActive());

        usuarioCredencial.setActive(false);
        assertFalse(usuarioCredencial.isActive());
    }
}
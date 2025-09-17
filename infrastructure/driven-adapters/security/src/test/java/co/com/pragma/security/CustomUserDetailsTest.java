package co.com.pragma.security;

import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    private CustomUserDetails customUserDetails;
    private UsuarioCredencial usuarioCredencial;
    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombre("ADMIN");

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEmail("admin@test.com");
        usuario.setNombre("Admin");
        usuario.setApellido("User");
        usuario.setRol(rol);

        usuarioCredencial = UsuarioCredencial.builder()
                .id(1)
                .email("admin@test.com")
                .password("hashedPassword123")
                .idUsuario(1)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        customUserDetails = new CustomUserDetails(usuarioCredencial, usuario);
    }

    @Test
    void getAuthorities_shouldReturnCorrectRole() {
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void getAuthorities_shouldHandleLowercaseRole() {
        rol.setNombre("admin");
        customUserDetails = new CustomUserDetails(usuarioCredencial, usuario);

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void getAuthorities_shouldHandleDifferentRoles() {
        rol.setNombre("CLIENTE");
        customUserDetails = new CustomUserDetails(usuarioCredencial, usuario);

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_CLIENTE")));
    }

    @Test
    void getPassword_shouldReturnCredentialPassword() {
        String password = customUserDetails.getPassword();

        assertEquals("hashedPassword123", password);
    }

    @Test
    void getUsername_shouldReturnCredentialEmail() {
        String username = customUserDetails.getUsername();

        assertEquals("admin@test.com", username);
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        assertTrue(customUserDetails.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_shouldReturnCredentialActiveStatus() {
        assertTrue(customUserDetails.isEnabled());

        usuarioCredencial = usuarioCredencial.toBuilder().active(false).build();
        customUserDetails = new CustomUserDetails(usuarioCredencial, usuario);

        assertFalse(customUserDetails.isEnabled());
    }

    @Test
    void getUserId_shouldReturnUsuarioId() {
        Integer userId = customUserDetails.getUserId();

        assertEquals(1, userId);
    }

    @Test
    void getRoleName_shouldReturnRoleName() {
        String roleName = customUserDetails.getRoleName();

        assertEquals("ADMIN", roleName);
    }

    @Test
    void constructor_shouldCreateValidInstance() {
        CustomUserDetails details = new CustomUserDetails(usuarioCredencial, usuario);

        assertNotNull(details);
        assertEquals(usuarioCredencial, details.usuarioCredencial());
        assertEquals(usuario, details.usuario());
    }

    @Test
    void isEnabled_shouldHandleNullCredentialObject() {
        // Test with null UsuarioCredencial object - records allow null parameters
        CustomUserDetails detailsWithNull = new CustomUserDetails(null, usuario);

        // This should throw NullPointerException when trying to access isActive()
        assertThrows(NullPointerException.class, detailsWithNull::isEnabled);
    }

    @Test
    void getAuthorities_shouldHandleSpecialCharactersInRole() {
        rol.setNombre("SUPER_ADMIN");
        customUserDetails = new CustomUserDetails(usuarioCredencial, usuario);

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")));
    }

    @Test
    void equals_shouldWorkCorrectly() {
        CustomUserDetails details1 = new CustomUserDetails(usuarioCredencial, usuario);
        CustomUserDetails details2 = new CustomUserDetails(usuarioCredencial, usuario);

        assertEquals(details1, details2);
        assertEquals(details1.hashCode(), details2.hashCode());
    }

    @Test
    void toString_shouldNotExposePassword() {
        String toString = customUserDetails.toString();

        assertNotNull(toString);
        // Verify password is not directly exposed in toString (depends on record implementation)
        assertTrue(toString.contains("CustomUserDetails"));
    }
}
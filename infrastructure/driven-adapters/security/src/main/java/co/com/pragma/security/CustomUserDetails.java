package co.com.pragma.security;

import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public record CustomUserDetails(UsuarioCredencial usuarioCredencial, Usuario usuario) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertir el rol del usuario a GrantedAuthority
        String roleName = "ROLE_" + usuario.getRol().getNombre().toUpperCase();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return usuarioCredencial.getPassword();
    }

    @Override
    public String getUsername() {
        return usuarioCredencial.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Por ahora siempre true, se puede implementar lógica específica
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Por ahora siempre true, se puede implementar lógica específica
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Por ahora siempre true, se puede implementar lógica específica
    }

    @Override
    public boolean isEnabled() {
        return usuarioCredencial.isActive();
    }

    public Integer getUserId() {
        return usuario.getIdUsuario();
    }

    public String getRoleName() {
        return usuario.getRol().getNombre();
    }
}
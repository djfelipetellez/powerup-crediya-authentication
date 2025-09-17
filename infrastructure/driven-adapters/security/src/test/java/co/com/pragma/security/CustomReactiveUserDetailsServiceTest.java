package co.com.pragma.security;

import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomReactiveUserDetailsServiceTest {

    @Mock
    private UsuarioCredencialRepository usuarioCredencialRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomReactiveUserDetailsService userDetailsService;

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
    }

    @Test
    void findByUsername_shouldReturnUserDetails_whenUserExists() {
        String email = "admin@test.com";

        when(usuarioCredencialRepository.findByEmail(email))
                .thenReturn(Mono.just(usuarioCredencial));
        when(usuarioRepository.findById(1))
                .thenReturn(Mono.just(usuario));

        StepVerifier.create(userDetailsService.findByUsername(email))
                .expectNextMatches(userDetails -> {
                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                    return customUserDetails.getUsername().equals(email) &&
                            customUserDetails.getUserId().equals(1) &&
                            customUserDetails.getRoleName().equals("ADMIN") &&
                            customUserDetails.isEnabled();
                })
                .verifyComplete();

        verify(usuarioCredencialRepository).findByEmail(email);
        verify(usuarioRepository).findById(1);
    }

    @Test
    void findByUsername_shouldThrowException_whenCredentialNotFound() {
        String email = "nonexistent@test.com";

        when(usuarioCredencialRepository.findByEmail(email))
                .thenReturn(Mono.empty());

        StepVerifier.create(userDetailsService.findByUsername(email))
                .expectErrorMatches(throwable ->
                        throwable instanceof UsernameNotFoundException &&
                                throwable.getMessage().equals("Usuario no encontrado con email: " + email)
                )
                .verify();

        verify(usuarioCredencialRepository).findByEmail(email);
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void findByUsername_shouldThrowException_whenUsuarioNotFound() {
        String email = "admin@test.com";

        when(usuarioCredencialRepository.findByEmail(email))
                .thenReturn(Mono.just(usuarioCredencial));
        when(usuarioRepository.findById(1))
                .thenReturn(Mono.empty());

        StepVerifier.create(userDetailsService.findByUsername(email))
                .expectErrorMatches(throwable ->
                        throwable instanceof UsernameNotFoundException &&
                                throwable.getMessage().equals("Datos de usuario no encontrados para email: " + email)
                )
                .verify();

        verify(usuarioCredencialRepository).findByEmail(email);
        verify(usuarioRepository).findById(1);
    }

    @Test
    void findByUsername_shouldHandleRepositoryError() {
        String email = "admin@test.com";
        RuntimeException repositoryError = new RuntimeException("Database connection error");

        when(usuarioCredencialRepository.findByEmail(email))
                .thenReturn(Mono.error(repositoryError));

        StepVerifier.create(userDetailsService.findByUsername(email))
                .expectError(RuntimeException.class)
                .verify();

        verify(usuarioCredencialRepository).findByEmail(email);
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void findByUsername_shouldHandleUsuarioRepositoryError() {
        String email = "admin@test.com";
        RuntimeException repositoryError = new RuntimeException("Usuario database error");

        when(usuarioCredencialRepository.findByEmail(email))
                .thenReturn(Mono.just(usuarioCredencial));
        when(usuarioRepository.findById(1))
                .thenReturn(Mono.error(repositoryError));

        StepVerifier.create(userDetailsService.findByUsername(email))
                .expectError(RuntimeException.class)
                .verify();

        verify(usuarioCredencialRepository).findByEmail(email);
        verify(usuarioRepository).findById(1);
    }

    @Test
    void findByUsername_shouldReturnCorrectUserDetails_forDifferentRoles() {
        String email = "cliente@test.com";
        rol.setNombre("CLIENTE");
        usuarioCredencial = usuarioCredencial.toBuilder().email(email).build();
        usuario.setEmail(email);

        when(usuarioCredencialRepository.findByEmail(email))
                .thenReturn(Mono.just(usuarioCredencial));
        when(usuarioRepository.findById(1))
                .thenReturn(Mono.just(usuario));

        StepVerifier.create(userDetailsService.findByUsername(email))
                .expectNextMatches(userDetails -> {
                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                    return customUserDetails.getUsername().equals(email) &&
                            customUserDetails.getRoleName().equals("CLIENTE") &&
                            customUserDetails.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENTE"));
                })
                .verifyComplete();

        verify(usuarioCredencialRepository).findByEmail(email);
        verify(usuarioRepository).findById(1);
    }

    @Test
    void findByUsername_shouldReturnInactiveUser() {
        String email = "inactive@test.com";
        usuarioCredencial = usuarioCredencial.toBuilder()
                .email(email)
                .active(false)
                .build();
        usuario.setEmail(email);

        when(usuarioCredencialRepository.findByEmail(email))
                .thenReturn(Mono.just(usuarioCredencial));
        when(usuarioRepository.findById(1))
                .thenReturn(Mono.just(usuario));

        StepVerifier.create(userDetailsService.findByUsername(email))
                .expectNextMatches(userDetails -> {
                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                    return customUserDetails.getUsername().equals(email) &&
                            !customUserDetails.isEnabled();
                })
                .verifyComplete();

        verify(usuarioCredencialRepository).findByEmail(email);
        verify(usuarioRepository).findById(1);
    }

    @Test
    void findByUsername_shouldHandleEmptyEmail() {
        String emptyEmail = "";

        when(usuarioCredencialRepository.findByEmail(emptyEmail))
                .thenReturn(Mono.empty());

        StepVerifier.create(userDetailsService.findByUsername(emptyEmail))
                .expectErrorMatches(throwable ->
                        throwable instanceof UsernameNotFoundException &&
                                throwable.getMessage().equals("Usuario no encontrado con email: ")
                )
                .verify();

        verify(usuarioCredencialRepository).findByEmail(emptyEmail);
    }

    @Test
    void findByUsername_shouldHandleNullEmail() {
        String nullEmail = null;

        when(usuarioCredencialRepository.findByEmail(nullEmail))
                .thenReturn(Mono.empty());

        StepVerifier.create(userDetailsService.findByUsername(nullEmail))
                .expectErrorMatches(throwable ->
                        throwable instanceof UsernameNotFoundException &&
                                throwable.getMessage().equals("Usuario no encontrado con email: null")
                )
                .verify();

        verify(usuarioCredencialRepository).findByEmail(nullEmail);
    }

    @Test
    void findByUsername_shouldReturnUserDetailsInterface() {
        String email = "admin@test.com";

        when(usuarioCredencialRepository.findByEmail(email))
                .thenReturn(Mono.just(usuarioCredencial));
        when(usuarioRepository.findById(1))
                .thenReturn(Mono.just(usuario));

        StepVerifier.create(userDetailsService.findByUsername(email))
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }
}
package co.com.pragma.usecase.auth;

import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LogGateway logGateway;

    private AuthorizationUseCase authorizationUseCase;

    @BeforeEach
    void setUp() {
        authorizationUseCase = new AuthorizationUseCase(usuarioRepository, logGateway);
    }

    @Test
    void validateUserRegistrationPermission_adminUser_shouldPass() {
        Integer userId = 1;
        Usuario adminUser = createUserWithRole("ADMIN");

        when(usuarioRepository.findById(userId)).thenReturn(Mono.just(adminUser));

        StepVerifier.create(authorizationUseCase.validateUserRegistrationPermission(userId))
                .verifyComplete();

        verify(logGateway, times(2)).info(eq("AuthorizationUseCase"), anyString());
        verify(usuarioRepository).findById(userId);
    }

    @Test
    void validateUserRegistrationPermission_asesorUser_shouldPass() {
        Integer userId = 2;
        Usuario asesorUser = createUserWithRole("ASESOR");

        when(usuarioRepository.findById(userId)).thenReturn(Mono.just(asesorUser));

        StepVerifier.create(authorizationUseCase.validateUserRegistrationPermission(userId))
                .verifyComplete();

        verify(logGateway, times(2)).info(eq("AuthorizationUseCase"), anyString());
        verify(usuarioRepository).findById(userId);
    }

    @Test
    void validateUserRegistrationPermission_clienteUser_shouldFail() {
        Integer userId = 3;
        Usuario clienteUser = createUserWithRole("CLIENTE");

        when(usuarioRepository.findById(userId)).thenReturn(Mono.just(clienteUser));

        StepVerifier.create(authorizationUseCase.validateUserRegistrationPermission(userId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("No tienes permisos para registrar usuarios"))
                .verify();

        verify(logGateway, times(2)).info(eq("AuthorizationUseCase"), anyString());
        verify(usuarioRepository).findById(userId);
    }

    @Test
    void validateUserRegistrationPermission_userNotFound_shouldFail() {
        Integer userId = 999;

        when(usuarioRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(authorizationUseCase.validateUserRegistrationPermission(userId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Usuario no encontrado"))
                .verify();

        verify(logGateway).info(eq("AuthorizationUseCase"), anyString());
        verify(usuarioRepository).findById(userId);
    }

    @Test
    void validateLoanCreationPermission_clienteUserForSelf_shouldPass() {
        Integer userId = 1;
        Integer targetUserId = 1;
        Usuario clienteUser = createUserWithRole("CLIENTE");

        when(usuarioRepository.findById(userId)).thenReturn(Mono.just(clienteUser));

        StepVerifier.create(authorizationUseCase.validateLoanCreationPermission(userId, targetUserId))
                .verifyComplete();

        verify(logGateway, times(2)).info(eq("AuthorizationUseCase"), anyString());
        verify(usuarioRepository).findById(userId);
    }

    @Test
    void validateLoanCreationPermission_clienteUserForOther_shouldFail() {
        Integer userId = 1;
        Integer targetUserId = 2;
        Usuario clienteUser = createUserWithRole("CLIENTE");

        when(usuarioRepository.findById(userId)).thenReturn(Mono.just(clienteUser));

        StepVerifier.create(authorizationUseCase.validateLoanCreationPermission(userId, targetUserId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Solo puedes crear solicitudes de préstamo para ti mismo"))
                .verify();

        verify(logGateway, times(2)).info(eq("AuthorizationUseCase"), anyString());
        verify(usuarioRepository).findById(userId);
    }

    @Test
    void validateLoanCreationPermission_adminUser_shouldPass() {
        Integer userId = 1;
        Integer targetUserId = 2;
        Usuario adminUser = createUserWithRole("ADMIN");

        when(usuarioRepository.findById(userId)).thenReturn(Mono.just(adminUser));

        StepVerifier.create(authorizationUseCase.validateLoanCreationPermission(userId, targetUserId))
                .verifyComplete();

        verify(logGateway, times(2)).info(eq("AuthorizationUseCase"), anyString());
        verify(usuarioRepository).findById(userId);
    }

    @Test
    void validateLoanCreationPermission_asesorUser_shouldPass() {
        Integer userId = 1;
        Integer targetUserId = 2;
        Usuario asesorUser = createUserWithRole("ASESOR");

        when(usuarioRepository.findById(userId)).thenReturn(Mono.just(asesorUser));

        StepVerifier.create(authorizationUseCase.validateLoanCreationPermission(userId, targetUserId))
                .verifyComplete();

        verify(logGateway, times(2)).info(eq("AuthorizationUseCase"), anyString());
        verify(usuarioRepository).findById(userId);
    }

    @Test
    void validateLoanCreationPermission_userNotFound_shouldFail() {
        Integer userId = 999;
        Integer targetUserId = 1;

        when(usuarioRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(authorizationUseCase.validateLoanCreationPermission(userId, targetUserId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Usuario no encontrado"))
                .verify();

        verify(logGateway).info(eq("AuthorizationUseCase"), anyString());
        verify(usuarioRepository).findById(userId);
    }

    @Test
    void getCurrentUser_userExists_shouldReturnUser() {
        Integer userId = 1;
        Usuario user = createUserWithRole("ADMIN");

        when(usuarioRepository.findById(userId)).thenReturn(Mono.just(user));

        StepVerifier.create(authorizationUseCase.getCurrentUser(userId))
                .expectNext(user)
                .verifyComplete();

        verify(usuarioRepository).findById(userId);
    }

    @Test
    void getCurrentUser_userNotFound_shouldFail() {
        Integer userId = 999;

        when(usuarioRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(authorizationUseCase.getCurrentUser(userId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Usuario no encontrado"))
                .verify();

        verify(usuarioRepository).findById(userId);
    }

    private Usuario createUserWithRole(String roleName) {
        Rol rol = new Rol();
        rol.setNombre(roleName);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEmail("test@test.com");
        usuario.setRol(rol);

        return usuario;
    }
}
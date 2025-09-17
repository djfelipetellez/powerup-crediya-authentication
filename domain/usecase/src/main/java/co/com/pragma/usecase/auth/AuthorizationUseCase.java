package co.com.pragma.usecase.auth;

import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthorizationUseCase {

    private final UsuarioRepository usuarioRepository;
    private final LogGateway loggingGateway;

    // Roles esperados en el sistema
    private static final String ROL_ADMIN = "ADMIN";
    private static final String ROL_ASESOR = "ASESOR";
    private static final String ROL_CLIENTE = "CLIENTE";

    public Mono<Void> validateUserRegistrationPermission(Integer usuarioId) {
        loggingGateway.info("AuthorizationUseCase", "Validando permisos de registro para usuario: " + usuarioId);

        return usuarioRepository.findById(usuarioId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuario no encontrado")))
                .flatMap(usuario -> {
                    String rol = usuario.getRol().getNombre().toUpperCase();
                    if (ROL_ADMIN.equals(rol) || ROL_ASESOR.equals(rol)) {
                        loggingGateway.info("AuthorizationUseCase", "Usuario autorizado para registrar usuarios: " + usuarioId);
                        return Mono.empty();
                    } else {
                        loggingGateway.info("AuthorizationUseCase", "Usuario sin permisos para registrar usuarios: " + usuarioId);
                        return Mono.error(new IllegalArgumentException("No tienes permisos para registrar usuarios"));
                    }
                });
    }

    public Mono<Void> validateLoanCreationPermission(Integer usuarioId, Integer targetUsuarioId) {
        loggingGateway.info("AuthorizationUseCase", "Validando permisos de creación de préstamo para usuario: " + usuarioId);

        return usuarioRepository.findById(usuarioId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuario no encontrado")))
                .flatMap(usuario -> {
                    String rol = usuario.getRol().getNombre().toUpperCase();
                    if (ROL_CLIENTE.equals(rol)) {
                        // Un cliente solo puede crear préstamos para sí mismo
                        if (!usuarioId.equals(targetUsuarioId)) {
                            loggingGateway.info("AuthorizationUseCase", "Cliente intentando crear préstamo para otro usuario: " + usuarioId + " -> " + targetUsuarioId);
                            return Mono.error(new IllegalArgumentException("Solo puedes crear solicitudes de préstamo para ti mismo"));
                        }
                        loggingGateway.info("AuthorizationUseCase", "Cliente autorizado para crear su propio préstamo: " + usuarioId);
                        return Mono.empty();
                    } else if (ROL_ADMIN.equals(rol) || ROL_ASESOR.equals(rol)) {
                        // Admin y asesor pueden crear préstamos para cualquier usuario
                        loggingGateway.info("AuthorizationUseCase", "Admin/Asesor autorizado para crear préstamo: " + usuarioId);
                        return Mono.empty();
                    } else {
                        loggingGateway.info("AuthorizationUseCase", "Usuario sin permisos para crear préstamos: " + usuarioId);
                        return Mono.error(new IllegalArgumentException("No tienes permisos para crear solicitudes de préstamo"));
                    }
                });
    }

    public Mono<Usuario> getCurrentUser(Integer usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuario no encontrado")));
    }

}
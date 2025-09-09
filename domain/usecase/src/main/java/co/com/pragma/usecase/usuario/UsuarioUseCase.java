package co.com.pragma.usecase.usuario;

import co.com.pragma.model.auth.UserCredential;
import co.com.pragma.model.auth.gateways.UserCredencialRepository;
import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.exceptions.UsuarioNotFoundException;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioValidator usuarioValidator;
    private final UserCredencialRepository userCredencialRepository;
    private final LogGateway loggingGateway;

    public Mono<Usuario> registrarUsuario(Usuario usuario, Integer roleId, String password) {
        loggingGateway.info("UsuarioUseCase", "Iniciando registro de usuario: " + roleId);

        return Mono.justOrEmpty(usuario)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constantes.MSG_USUARIO_NULL)))
                .flatMap(validatedUser -> usuarioValidator.validate(validatedUser, roleId)
                        .then(rolRepository.findById(roleId)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constantes.MSG_ROLE_NOT_EXISTS)))
                                .then())
                        .then(usuarioRepository.registrarUsuarioCompleto(validatedUser, roleId))
                )
                .flatMap(usuarioRegistrado -> crearCredencialesUsuario(usuarioRegistrado, password)
                        .thenReturn(usuarioRegistrado)
                )
                .doOnSuccess(usuarioRegistrado ->
                        loggingGateway.info("UsuarioUseCase", "Usuario y credenciales registradas exitosamente: " + usuarioRegistrado.getEmail())
                )
                .doOnError(error ->
                        loggingGateway.error("UsuarioUseCase", "Error al registrar usuario: " + error.getMessage(), error)
                );
    }

    private Mono<UserCredential> crearCredencialesUsuario(Usuario usuario, String password) {
        UserCredential credential = UserCredential.builder()
                .email(usuario.getEmail())
                .password(password) // TODO: Encriptar con BCrypt
                .usuarioId(usuario.getIdUsuario())
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        return userCredencialRepository.save(credential);
    }

    public Mono<Void> validarExistenciaUsuario(String documentoIdentidad, String email) {
        loggingGateway.info("UsuarioUseCase", "Validando datos de usuario: documento=" + documentoIdentidad + ", email=" + email);

        return usuarioRepository.findByDocumentoIdentidadAndEmail(documentoIdentidad, email)
                .switchIfEmpty(Mono.error(new UsuarioNotFoundException("Usuario no encontrado con documento: " + documentoIdentidad + " y email: " + email)))
                .then()
                .doOnSuccess(result ->
                        loggingGateway.info("UsuarioUseCase", "Datos de usuario validados exitosamente"))
                .doOnError(error -> {
                    if (error instanceof UsuarioNotFoundException) {
                        loggingGateway.info("UsuarioUseCase", "Usuario no encontrado durante validación: " + error.getMessage());
                    } else {
                        loggingGateway.error("UsuarioUseCase", "Error validando existencia de usuario: " + error.getMessage(), error);
                    }
                });
    }


}
package co.com.pragma.usecase.usuario;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.exceptions.UsuarioNotFoundException;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioValidator usuarioValidator;
    private final LogGateway loggingGateway;

    public Mono<Usuario> registrarUsuario(Usuario usuario, Integer roleId, String password) {
        loggingGateway.info("UsuarioUseCase", "Iniciando registro de usuario: " + roleId);

        return Mono.justOrEmpty(usuario)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constantes.MSG_USUARIO_NULL)))
                .flatMap(validatedUser -> usuarioValidator.validate(validatedUser, roleId)
                        .then(rolRepository.findById(roleId)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constantes.MSG_ROLE_NOT_EXISTS)))
                                .then())
                        .then(usuarioRepository.registrarUsuarioCompleto(validatedUser, roleId, password))
                )
                .doOnSuccess(usuarioRegistrado ->
                        loggingGateway.info("UsuarioUseCase", "Usuario y credenciales registradas exitosamente: " + usuarioRegistrado.getEmail())
                )
                .doOnError(error ->
                        loggingGateway.error("UsuarioUseCase", "Error al registrar usuario: " + error.getMessage(), error)
                );
    }

    public Mono<Usuario> consultarUsuario(String email) {
        loggingGateway.info("UsuarioUseCase", "Consultando datos de usuario por email: " + email);

        return usuarioRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UsuarioNotFoundException("Usuario no encontrado con email: " + email)))
                .doOnSuccess(usuario ->
                        loggingGateway.info("UsuarioUseCase", "Usuario consultado exitosamente: " + usuario.getEmail()))
                .doOnError(error -> {
                    if (error instanceof UsuarioNotFoundException) {
                        loggingGateway.info("UsuarioUseCase", "Usuario no encontrado durante consulta: " + error.getMessage());
                    } else {
                        loggingGateway.error("UsuarioUseCase", "Error consultando usuario: " + error.getMessage(), error);
                    }
                });
    }

}
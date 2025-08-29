package co.com.pragma.usecase.usuario;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LoggingGateway;
import co.com.pragma.model.rol.gateways.RolValidator;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.model.usuario.gateways.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final RolValidator rolValidator;
    private final UsuarioValidator usuarioValidator;
    private final LoggingGateway loggingGateway;

    public Mono<Usuario> registrarUsuario(Usuario usuario, Integer roleId) {
        loggingGateway.info("UsuarioUseCase", "Iniciando registro de usuario: " + roleId);


        return  Mono.justOrEmpty(usuario)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constantes.MSG_USUARIO_NULL)))
                .flatMap(validatedUser -> usuarioValidator.validate(validatedUser, roleId)
                        .then(rolValidator.validarRolExiste(roleId))
                        .then(usuarioRepository.registrarUsuarioCompleto(validatedUser, roleId))
                )
                .doOnSuccess(usuarioRegistrado -> 
                        loggingGateway.info("UsuarioUseCase", "Usuario registrado exitosamente: " + usuarioRegistrado.getEmail())
                )
                .doOnError(error ->
                        loggingGateway.error("UsuarioUseCase", "Error al registrar usuario: " + error.getMessage(), error)
                );
    }


}
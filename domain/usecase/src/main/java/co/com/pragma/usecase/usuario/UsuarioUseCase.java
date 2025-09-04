package co.com.pragma.usecase.usuario;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.Usuario;
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

    public Mono<Usuario> registrarUsuario(Usuario usuario, Integer roleId) {
        loggingGateway.info("UsuarioUseCase", "Iniciando registro de usuario: " + roleId);


        return Mono.justOrEmpty(usuario)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constantes.MSG_USUARIO_NULL)))
                .flatMap(validatedUser -> usuarioValidator.validate(validatedUser, roleId)
                        .then(rolRepository.findById(roleId)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constantes.MSG_ROLE_NOT_EXISTS)))
                                .then())
                        .then(usuarioRepository.registrarUsuarioCompleto(validatedUser, roleId))
                )
                .doOnSuccess(usuarioRegistrado ->
                        loggingGateway.info("UsuarioUseCase", "Usuario registrado exitosamente: " + usuarioRegistrado.getEmail())
                )
                .doOnError(error ->
                        loggingGateway.error("UsuarioUseCase", "Error al registrar usuario: " + error.getMessage(), error)
                );
    }

    public Mono<Void> validarExistenciaUsuario(String documentoIdentidad, String email) {
        loggingGateway.info("UsuarioUseCase", "Validando datos de usuario: documento=" + documentoIdentidad + ", email=" + email);

        return usuarioRepository.findByDocumentoIdentidadAndEmail(documentoIdentidad, email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuario no encontrado con documento: " + documentoIdentidad + " y email: " + email)))
                .then()
                .doOnSuccess(result ->
                        loggingGateway.info("UsuarioUseCase", "Datos de usuario validados exitosamente"))
                .doOnError(error ->
                        loggingGateway.error("UsuarioUseCase", "Error validando existencia de usuario: " + error.getMessage(), error));
    }


}
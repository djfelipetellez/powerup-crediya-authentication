package co.com.pragma.api;

import co.com.pragma.api.dto.RolRegistroRequestDto;
import co.com.pragma.api.dto.UsuarioRegistroRequestDto;
import co.com.pragma.api.mapper.RolMapper;
import co.com.pragma.api.mapper.UsuarioMapper;
import co.com.pragma.api.util.LoggingUtil;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.usecase.rol.RolUseCase;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UsuarioUseCase usuarioUseCase;
    private final RolUseCase rolUseCase;
    private final UsuarioMapper usuarioMapper;
    private final RolMapper rolMapper;

    public Mono<ServerResponse> registrarUsuario(ServerRequest request) {
        return request.bodyToMono(UsuarioRegistroRequestDto.class)
                .flatMap(requestDto -> {
                    LoggingUtil.info("registrarUsuario", String.format("Intento de registro de usuario: email=%s, doc=%s", requestDto.email(), requestDto.documentoIdentidad()));

                    Usuario usuario = usuarioMapper.toDomain(requestDto);
                    Integer idRol = requestDto.idRol();

                    return usuarioUseCase.registrarUsuario(usuario, idRol)
                            .flatMap(saved -> {
                                LoggingUtil.info("registrarUsuario", String.format("Usuario registrado id=%d, email=%s", saved.getIdUsuario(), saved.getEmail()));
                                return ServerResponse.status(HttpStatus.CREATED)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(usuarioMapper.toResponseDto(saved));
                            })
                            .onErrorResume(IllegalArgumentException.class, ex -> {
                                LoggingUtil.warn("registrarUsuario", String.format("Validación falló al registrar usuario: %s - request: %s", ex.getMessage(), requestDto), ex);
                                return ServerResponse.badRequest().build();
                            })
                            .onErrorResume(ex -> {
                                LoggingUtil.error("registrarUsuario", "Error inesperado registrando usuario", ex);
                                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                            });
                });
    }

    public Mono<ServerResponse> registrarRol(ServerRequest request) {
        return request.bodyToMono(RolRegistroRequestDto.class)
                .flatMap(requestDto -> {
                    LoggingUtil.info("registrarRol", String.format("Intento de registro de rol: nombre=%s", requestDto.nombre()));

                    Rol rol = rolMapper.toDomain(requestDto);

                    return rolUseCase.registrarRol(rol)
                            .flatMap(saved -> {
                                LoggingUtil.info("registrarRol", String.format("Rol registrado id=%d, nombre=%s", saved.getIdRol(), saved.getNombre()));
                                return ServerResponse.status(HttpStatus.CREATED)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(rolMapper.toResponseDto(saved));
                            })
                            .onErrorResume(IllegalArgumentException.class, ex -> {
                                LoggingUtil.warn("registrarRol", String.format("Validación falló al registrar rol: %s - request: %s", ex.getMessage(), requestDto), ex);
                                return ServerResponse.badRequest().build();
                            })
                            .onErrorResume(ex -> {
                                LoggingUtil.error("registrarRol", "Error inesperado registrando rol", ex);
                                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                            });
                });
    }
}

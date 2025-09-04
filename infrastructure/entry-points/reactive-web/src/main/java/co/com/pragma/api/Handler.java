package co.com.pragma.api;

import co.com.pragma.api.dto.ClienteValidationRequest;
import co.com.pragma.api.dto.RolRegistroRequestDto;
import co.com.pragma.api.dto.UsuarioRegistroRequestDto;
import co.com.pragma.api.mapper.RolMapper;
import co.com.pragma.api.mapper.UsuarioMapper;
import co.com.pragma.api.util.RequestValidator;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.exceptions.UsuarioNotFoundException;
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
    private final RequestValidator requestValidator;
    private final LogGateway logGateway;


    public Mono<ServerResponse> registrarUsuario(ServerRequest request) {
        return request.bodyToMono(UsuarioRegistroRequestDto.class)
                .flatMap(requestValidator::validate)
                .flatMap(requestDto -> {
                    logGateway.info("registrarUsuario", String.format("Intento de registro de usuario: email=%s, doc=%s", requestDto.email(), requestDto.documentoIdentidad()));

                    Usuario usuario = usuarioMapper.toDomain(requestDto);
                    Integer idRol = requestDto.idRol();

                    return usuarioUseCase.registrarUsuario(usuario, idRol)
                            .flatMap(saved -> {
                                logGateway.info("registrarUsuario", String.format("Usuario registrado id=%d, email=%s", saved.getIdUsuario(), saved.getEmail()));
                                return ServerResponse.status(HttpStatus.CREATED)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(usuarioMapper.toResponseDto(saved));
                            });
                });
    }

    public Mono<ServerResponse> registrarRol(ServerRequest request) {
        return request.bodyToMono(RolRegistroRequestDto.class)
                .flatMap(requestValidator::validate)
                .flatMap(requestDto -> {
                    logGateway.info("registrarRol", String.format("Intento de registro de rol: nombre=%s", requestDto.nombre()));

                    Rol rol = rolMapper.toDomain(requestDto);

                    return rolUseCase.registrarRol(rol)
                            .flatMap(saved -> {
                                logGateway.info("registrarRol", String.format("Rol registrado id=%d, nombre=%s", saved.getIdRol(), saved.getNombre()));
                                return ServerResponse.status(HttpStatus.CREATED)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(rolMapper.toResponseDto(saved));
                            });
                });
    }

    public Mono<ServerResponse> validarExistenciaUsuario(ServerRequest request) {
        logGateway.info("Handler", "Iniciando validación de existencia de usuario");

        return request.bodyToMono(ClienteValidationRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(validationRequest -> usuarioUseCase.validarExistenciaUsuario(
                        validationRequest.documentoIdentidad(),
                        validationRequest.email()))
                .then(ServerResponse.ok().build())
                .doOnSuccess(response -> logGateway.info("Handler", "Validación de existencia completada"))
                .doOnError(error -> {
                    if (error instanceof UsuarioNotFoundException) {
                        logGateway.info("Handler", "Usuario no encontrado durante validación: " + error.getMessage());
                    } else {
                        logGateway.error("Handler", "Error validando existencia: " + error.getMessage(), error);
                    }
                });
    }
}

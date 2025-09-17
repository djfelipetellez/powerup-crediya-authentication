package co.com.pragma.api;

import co.com.pragma.api.dto.ClienteValidationRequest;
import co.com.pragma.api.dto.LoginRequestDto;
import co.com.pragma.api.dto.RolRegistroRequestDto;
import co.com.pragma.api.dto.TokenValidationRequestDto;
import co.com.pragma.api.dto.TokenValidationResponseDto;
import co.com.pragma.api.dto.UsuarioRegistroRequestDto;
import co.com.pragma.api.mapper.RolMapper;
import co.com.pragma.api.mapper.UsuarioMapper;
import co.com.pragma.api.util.RequestValidator;
import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.exceptions.UsuarioNotFoundException;
import co.com.pragma.usecase.auth.LoginAuthenticationUseCase;
import co.com.pragma.usecase.rol.RolUseCase;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UsuarioUseCase usuarioUseCase;
    private final RolUseCase rolUseCase;
    private final LoginAuthenticationUseCase loginAuthenticationUseCase;
    private final UsuarioMapper usuarioMapper;
    private final RolMapper rolMapper;
    private final RequestValidator requestValidator;
    private final LogGateway logGateway;


    @PreAuthorize("hasRole('ADMIN') or hasRole('ASESOR')")
    public Mono<ServerResponse> registrarUsuario(ServerRequest request) {
        return request.bodyToMono(UsuarioRegistroRequestDto.class)
                .flatMap(requestValidator::validate)
                .flatMap(requestDto -> {
                    logGateway.info("registrarUsuario", String.format("Intento de registro de usuario: email=%s, doc=%s", requestDto.email(), requestDto.documentoIdentidad()));

                    Usuario usuario = usuarioMapper.toDomain(requestDto);
                    Integer idRol = requestDto.idRol();
                    String password = requestDto.password();

                    return usuarioUseCase.registrarUsuario(usuario, idRol, password)
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

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequestDto.class)
                .flatMap(requestValidator::validate)
                .flatMap(loginDto -> {
                    logGateway.info("login", "Intento de login para email: " + loginDto.email());

                    LoginCredenciales credentials = new LoginCredenciales(loginDto.email(), loginDto.password());

                    return loginAuthenticationUseCase.login(credentials)
                            .flatMap(tokenAuth -> {
                                logGateway.info("login", "Login exitoso para email: " + loginDto.email());
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(Map.of(
                                                "token", tokenAuth.token(),
                                                "message", "Login exitoso"
                                        ));
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

    public Mono<ServerResponse> validateToken(ServerRequest request) {
        logGateway.info("Handler", "=== INICIANDO validateToken endpoint ===");
        
        return request.bodyToMono(TokenValidationRequestDto.class)
                .doOnNext(req -> logGateway.info("Handler", "Token recibido para validación: " + req.token().substring(0, Math.min(20, req.token().length())) + "..."))
                .flatMap(requestValidator::validate)
                .doOnNext(req -> logGateway.info("Handler", "Request validado correctamente"))
                .flatMap(tokenRequest -> {
                    logGateway.info("Handler", "Enviando token al use case para validación");

                    return loginAuthenticationUseCase.validateToken(tokenRequest.token())
                            .doOnNext(result -> logGateway.info("Handler", "Resultado del use case - válido: " + result.valid() + ", error: " + result.error()))
                            .flatMap(result -> {
                                TokenValidationResponseDto response = new TokenValidationResponseDto(
                                        result.valid(),
                                        result.userId(),
                                        result.email(),
                                        result.role(),
                                        result.documentoIdentidad(),
                                        result.exp(),
                                        result.error()
                                );

                                logGateway.info("Handler", "=== FINALIZANDO validateToken endpoint - Respuesta: " + response.valid() + " ===");

                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(response);
                            });
                })
                .doOnError(error -> logGateway.error("Handler", "Error en validateToken: " + error.getMessage(), error));
    }
}

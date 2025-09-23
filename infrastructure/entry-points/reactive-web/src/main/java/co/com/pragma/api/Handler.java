package co.com.pragma.api;

import co.com.pragma.api.dto.*;
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

import static co.com.pragma.api.util.ApiConstantes.*;

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
                    logGateway.info(LOG_REGISTRAR_USUARIO, String.format(MSG_INTENTO_REGISTRO_USUARIO, requestDto.email(), requestDto.documentoIdentidad()));

                    Usuario usuario = usuarioMapper.toDomain(requestDto);
                    Integer idRol = requestDto.idRol();
                    String password = requestDto.password();

                    return usuarioUseCase.registrarUsuario(usuario, idRol, password)
                            .flatMap(saved -> {
                                logGateway.info(LOG_REGISTRAR_USUARIO, String.format(MSG_USUARIO_REGISTRADO, saved.getIdUsuario(), saved.getEmail()));
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
                    logGateway.info(LOG_REGISTRAR_ROL, String.format(MSG_INTENTO_REGISTRO_ROL, requestDto.nombre()));

                    Rol rol = rolMapper.toDomain(requestDto);

                    return rolUseCase.registrarRol(rol)
                            .flatMap(saved -> {
                                logGateway.info(LOG_REGISTRAR_ROL, String.format(MSG_ROL_REGISTRADO, saved.getIdRol(), saved.getNombre()));
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
                    logGateway.info(LOG_LOGIN, String.format(MSG_INTENTO_LOGIN, loginDto.email()));

                    LoginCredenciales credentials = new LoginCredenciales(loginDto.email(), loginDto.password());

                    return loginAuthenticationUseCase.login(credentials)
                            .flatMap(tokenAuth -> {
                                logGateway.info(LOG_LOGIN, String.format(MSG_LOGIN_EXITOSO_EMAIL, loginDto.email()));
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(Map.of(
                                                RESPONSE_TOKEN_KEY, tokenAuth.token(),
                                                RESPONSE_MESSAGE_KEY, RESPONSE_LOGIN_EXITOSO
                                        ));
                            });
                });
    }

    public Mono<ServerResponse> consultarUsuario(ServerRequest request) {
        logGateway.info(LOG_HANDLER, MSG_INICIANDO_CONSULTAR_USUARIO);

        String email = request.pathVariable("email");
        logGateway.info(LOG_HANDLER, String.format(MSG_DATOS_ENTRANTES_EMAIL, email));

        return Mono.just(email)
                .doOnNext(emailParam -> logGateway.info(LOG_HANDLER, String.format(MSG_PATH_PARAMETER_EXTRAIDO, emailParam)))
                .flatMap(emailParam -> {
                    logGateway.info(LOG_HANDLER, String.format(MSG_ENVIANDO_CONSULTA_USE_CASE, emailParam));
                    return usuarioUseCase.consultarUsuario(emailParam);
                })
                .flatMap(usuario -> {
                    logGateway.info(LOG_HANDLER, String.format(MSG_USUARIO_ENCONTRADO, usuario.toString()));
                    UsuarioResponseDto responseDto = usuarioMapper.toResponseDto(usuario);
                    logGateway.info(LOG_HANDLER, String.format(MSG_DATOS_SALIENTES_RESPUESTA, responseDto.toString()));
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(responseDto);
                })
                .doOnSuccess(response -> logGateway.info(LOG_HANDLER, MSG_FINALIZANDO_CONSULTAR_USUARIO))
                .doOnError(error -> {
                    if (error instanceof UsuarioNotFoundException) {
                        logGateway.info(LOG_HANDLER, String.format(MSG_USUARIO_NO_ENCONTRADO_CONSULTA, error.getMessage()));
                    } else {
                        logGateway.error(LOG_HANDLER, String.format(MSG_ERROR_CONSULTANDO_USUARIO, error.getMessage()), error);
                    }
                });
    }

    public Mono<ServerResponse> validateToken(ServerRequest request) {
        logGateway.info(LOG_HANDLER, MSG_INICIANDO_VALIDATE_TOKEN);

        return request.bodyToMono(TokenValidationRequestDto.class)
                .doOnNext(req -> logGateway.info(LOG_HANDLER, String.format(MSG_TOKEN_RECIBIDO_VALIDACION, req.token().substring(0, Math.min(20, req.token().length())))))
                .flatMap(requestValidator::validate)
                .doOnNext(req -> logGateway.info(LOG_HANDLER, MSG_REQUEST_VALIDADO))
                .flatMap(tokenRequest -> {
                    logGateway.info(LOG_HANDLER, MSG_ENVIANDO_TOKEN_USE_CASE);

                    return loginAuthenticationUseCase.validateToken(tokenRequest.token())
                            .doOnNext(result -> logGateway.info(LOG_HANDLER, String.format(MSG_RESULTADO_USE_CASE, result.valid(), result.error())))
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

                                logGateway.info(LOG_HANDLER, String.format(MSG_FINALIZANDO_VALIDATE_TOKEN, response.valid()));

                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(response);
                            });
                })
                .doOnError(error -> logGateway.error(LOG_HANDLER, String.format(MSG_ERROR_VALIDATE_TOKEN, error.getMessage()), error));
    }
}

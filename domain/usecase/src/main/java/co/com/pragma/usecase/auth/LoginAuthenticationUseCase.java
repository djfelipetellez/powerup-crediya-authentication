package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.auth.TokenAutenticacion;
import co.com.pragma.model.auth.TokenValidationResult;
import co.com.pragma.model.auth.gateways.AuthenticationGateway;
import co.com.pragma.model.common.gateways.LogGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginAuthenticationUseCase {

    private final AuthenticationGateway authenticationGateway;
    private final LogGateway logGateway;

    public Mono<TokenAutenticacion> login(LoginCredenciales loginCredenciales) {
        logGateway.info("LoginAuthenticationUseCase", "Iniciando proceso de login para: " + loginCredenciales.email());

        return authenticationGateway.authenticateLogIn(loginCredenciales)
                .doOnSuccess(token ->
                        logGateway.info("LoginAuthenticationUseCase", "Login exitoso para: " + loginCredenciales.email()))
                .doOnError(error ->
                        logGateway.warn("LoginAuthenticationUseCase", "Error en login para: " + loginCredenciales.email() + " - " + error.getMessage(), error));
    }

    public Mono<TokenValidationResult> validateToken(String token) {
        logGateway.info("LoginAuthenticationUseCase", "Iniciando validación de token");

        return authenticationGateway.validateToken(token)
                .doOnSuccess(result -> {
                    if (result.valid()) {
                        logGateway.info("LoginAuthenticationUseCase", "Token válido para usuario: " + result.email());
                    } else {
                        logGateway.info("LoginAuthenticationUseCase", "Token inválido: " + result.error());
                    }
                })
                .doOnError(error ->
                        logGateway.error("LoginAuthenticationUseCase", "Error validando token: " + error.getMessage(), error));
    }

}
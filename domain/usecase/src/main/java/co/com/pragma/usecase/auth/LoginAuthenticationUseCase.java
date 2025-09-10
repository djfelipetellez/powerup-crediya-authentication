package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.auth.TokenAutenticacion;
import co.com.pragma.model.auth.gateways.AuthenticationGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginAuthenticationUseCase {

    private final AuthenticationGateway authenticationGateway;

    public Mono<TokenAutenticacion> login(LoginCredenciales loginCredenciales) {
        return authenticationGateway.authenticateLogIn(loginCredenciales);
    }

}
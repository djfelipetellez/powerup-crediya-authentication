package co.com.pragma.model.auth.gateways;

import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.auth.TokenAutenticacion;
import reactor.core.publisher.Mono;

public interface AuthenticationGateway {

    Mono<TokenAutenticacion> authenticateLogIn(LoginCredenciales loginCredenciales);

}
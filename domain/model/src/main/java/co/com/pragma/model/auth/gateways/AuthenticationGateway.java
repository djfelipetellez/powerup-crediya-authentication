package co.com.pragma.model.auth.gateways;

import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.auth.TokenAutenticacion;
import co.com.pragma.model.auth.TokenValidationResult;
import reactor.core.publisher.Mono;

public interface AuthenticationGateway {

    Mono<TokenAutenticacion> authenticateLogIn(LoginCredenciales loginCredenciales);
    
    Mono<TokenValidationResult> validateToken(String token);

}
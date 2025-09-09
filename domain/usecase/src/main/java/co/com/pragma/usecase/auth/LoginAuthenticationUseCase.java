package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.auth.TokenAutenticacion;
import co.com.pragma.model.auth.UserCredential;
import co.com.pragma.model.auth.gateways.UserCredencialRepository;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginAuthenticationUseCase {

    private final UserCredencialRepository userCredentialRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogGateway loggingGateway;

    public Mono<TokenAutenticacion> login(LoginCredenciales credentials) {
        loggingGateway.info("AuthenticationUseCase", "Iniciando autenticación para email: " + credentials.email());

        return userCredentialRepository.findByEmail(credentials.email())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Credenciales inválidas")))
                .flatMap(userCredential -> validateCredentials(userCredential, credentials.password()))
                .flatMap(userCredential -> usuarioRepository.findById(userCredential.getUsuarioId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Usuario no encontrado")))
                .flatMap(this::generateToken)
                .doOnSuccess(token -> 
                    loggingGateway.info("AuthenticationUseCase", "Autenticación exitosa para email: " + credentials.email())
                )
                .doOnError(error ->
                    loggingGateway.error("AuthenticationUseCase", "Error en autenticación: " + error.getMessage(), error)
                );
    }

    private Mono<UserCredential> validateCredentials(UserCredential userCredential, String password) {
        // TODO: Implementar validación de contraseña encriptada
        if (!userCredential.isActive()) {
            return Mono.error(new IllegalArgumentException("Usuario inactivo"));
        }
        
        // Por ahora comparación simple, después se implementará con BCrypt
        if (!userCredential.getPassword().equals(password)) {
            return Mono.error(new IllegalArgumentException("Credenciales inválidas"));
        }
        
        return Mono.just(userCredential);
    }

    private Mono<TokenAutenticacion> generateToken(Usuario usuario) {
        // TODO: Implementar generación de JWT con los datos del usuario y su rol
        String token = "jwt-token-placeholder-" + usuario.getIdUsuario();
        return Mono.just(new TokenAutenticacion(token));
    }

}
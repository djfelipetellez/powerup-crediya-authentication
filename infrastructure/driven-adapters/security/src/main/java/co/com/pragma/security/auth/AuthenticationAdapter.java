package co.com.pragma.security.auth;

import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.auth.TokenAutenticacion;
import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.auth.exceptions.AuthenticationException;
import co.com.pragma.model.auth.gateways.AuthenticationGateway;
import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationAdapter implements AuthenticationGateway {

    private final UsuarioCredencialRepository usuarioCredencialRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final LogGateway logGateway;

    @Override
    public Mono<TokenAutenticacion> authenticateLogIn(LoginCredenciales loginCredenciales) {
        logGateway.info("AuthenticationAdapter", Constantes.LOG_AUTH_START + loginCredenciales.email());

        return usuarioCredencialRepository.findByEmail(loginCredenciales.email())
                .switchIfEmpty(Mono.error(new AuthenticationException(Constantes.MSG_INVALID_CREDENTIALS)))
                .flatMap(userCredential -> validateCredentials(userCredential, loginCredenciales.password()))
                .flatMap(userCredential -> usuarioRepository.findById(userCredential.getIdUsuario()))
                .switchIfEmpty(Mono.error(new AuthenticationException(Constantes.MSG_INVALID_CREDENTIALS)))
                .flatMap(this::generateToken)
                .onErrorMap(BadCredentialsException.class, ex -> new AuthenticationException(ex.getMessage(), ex))
                .doOnSuccess(token ->
                        logGateway.info("AuthenticationAdapter", Constantes.LOG_AUTH_SUCCESS + loginCredenciales.email())
                )
                .doOnError(error ->
                        logGateway.error("AuthenticationAdapter", Constantes.LOG_AUTH_ERROR + error.getMessage(), error)
                );
    }

    private Mono<UsuarioCredencial> validateCredentials(UsuarioCredencial usuarioCredencial, String password) {
        if (!usuarioCredencial.isActive()) {
            return Mono.error(new AuthenticationException(Constantes.MSG_USER_INACTIVE));
        }

        if (!passwordEncoder.matches(password, usuarioCredencial.getPassword())) {
            return Mono.error(new AuthenticationException(Constantes.MSG_INVALID_CREDENTIALS));
        }

        return Mono.just(usuarioCredencial);
    }

    private Mono<TokenAutenticacion> generateToken(Usuario usuario) {
        String token = jwtProvider.generateToken(
                usuario.getEmail(),
                usuario.getIdUsuario(),
                usuario.getRol().getNombre()
        );
        return Mono.just(new TokenAutenticacion(token));
    }
}
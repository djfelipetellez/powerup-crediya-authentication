package co.com.pragma.security.auth;

import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.auth.TokenAutenticacion;
import co.com.pragma.model.auth.TokenValidationResult;
import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.auth.exceptions.AuthenticationException;
import co.com.pragma.model.auth.gateways.AuthenticationGateway;
import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.security.jwt.provider.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

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
        long startTime = System.currentTimeMillis();
        logGateway.info("AuthenticationAdapter", Constantes.LOG_AUTH_START + loginCredenciales.email());

        return usuarioCredencialRepository.findByEmail(loginCredenciales.email())
                .doOnNext(userCredential ->
                        logGateway.info("AuthenticationAdapter", Constantes.LOG_USER_FOUND + loginCredenciales.email())
                )
                .switchIfEmpty(Mono.defer(() -> {
                    logGateway.warn("AuthenticationAdapter", Constantes.LOG_USER_NOT_FOUND + loginCredenciales.email(), null);
                    return Mono.error(new AuthenticationException(Constantes.MSG_INVALID_CREDENTIALS));
                }))
                .flatMap(userCredential -> validateCredentials(userCredential, loginCredenciales.password()))
                .flatMap(userCredential -> {
                    logGateway.debug("AuthenticationAdapter", Constantes.LOG_USER_DATA_FETCH + userCredential.getIdUsuario());
                    return usuarioRepository.findById(userCredential.getIdUsuario());
                })
                .switchIfEmpty(Mono.error(new AuthenticationException(Constantes.MSG_INVALID_CREDENTIALS)))
                .flatMap(this::generateToken)
                .onErrorMap(BadCredentialsException.class, ex -> new AuthenticationException(ex.getMessage(), ex))
                .doOnSuccess(token -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logGateway.info("AuthenticationAdapter", Constantes.LOG_AUTH_SUCCESS + loginCredenciales.email());
                    logGateway.debug("AuthenticationAdapter", Constantes.LOG_AUTH_DURATION + duration + "ms");
                })
                .doOnError(error ->
                        logGateway.error("AuthenticationAdapter", Constantes.LOG_AUTH_ERROR + error.getMessage(), error)
                );
    }

    private Mono<UsuarioCredencial> validateCredentials(UsuarioCredencial usuarioCredencial, String password) {
        logGateway.debug("AuthenticationAdapter", Constantes.LOG_USER_STATUS_CHECK + usuarioCredencial.getEmail());

        if (!usuarioCredencial.isActive()) {
            logGateway.warn("AuthenticationAdapter", Constantes.LOG_USER_INACTIVE_ATTEMPT + usuarioCredencial.getEmail(), null);
            return Mono.error(new AuthenticationException(Constantes.MSG_USER_INACTIVE));
        }

        logGateway.debug("AuthenticationAdapter", Constantes.LOG_PASSWORD_VALIDATION + usuarioCredencial.getEmail());
        if (!passwordEncoder.matches(password, usuarioCredencial.getPassword())) {
            logGateway.warn("AuthenticationAdapter", Constantes.LOG_PASSWORD_INVALID + usuarioCredencial.getEmail(), null);
            return Mono.error(new AuthenticationException(Constantes.MSG_INVALID_CREDENTIALS));
        }

        logGateway.debug("AuthenticationAdapter", "Credenciales válidas para email: " + usuarioCredencial.getEmail());
        return Mono.just(usuarioCredencial);
    }

    private Mono<TokenAutenticacion> generateToken(Usuario usuario) {
        logGateway.debug("AuthenticationAdapter", Constantes.LOG_TOKEN_GENERATION_START + usuario.getIdUsuario() + " con rol: " + usuario.getRol().getNombre());

        String token = jwtProvider.generateToken(
                usuario.getEmail(),
                usuario.getIdUsuario(),
                usuario.getRol().getNombre(),
                usuario.getDocumentoIdentidad()
        );

        logGateway.debug("AuthenticationAdapter", Constantes.LOG_TOKEN_GENERATION_SUCCESS + usuario.getIdUsuario());
        return Mono.just(new TokenAutenticacion(token));
    }

    @Override
    public Mono<TokenValidationResult> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                if (Boolean.TRUE.equals(jwtProvider.isTokenExpired(token))) {
                    logGateway.info("AuthenticationAdapter", "Token expirado");
                    return TokenValidationResult.invalid("Token expired");
                }

                Claims claims = jwtProvider.getClaims(token);
                Integer userId = jwtProvider.getUserIdFromToken(token);
                String email = jwtProvider.getUsernameFromToken(token);
                String documentoIdentidad = jwtProvider.getDocumentoIdentidadFromToken(token);
                Long exp = claims.getExpiration().getTime();

                List<Map<String, String>> roles = (List<Map<String, String>>) claims.get("roles");
                String role = roles != null && !roles.isEmpty()
                        ? roles.getFirst().get("authority").replace("ROLE_", "")
                        : "UNKNOWN";

                logGateway.info("AuthenticationAdapter", String.format("Token válido para usuario: %s, rol: %s", email, role));

                return TokenValidationResult.valid(userId, email, role, documentoIdentidad, exp);

            } catch (Exception e) {
                logGateway.error("AuthenticationAdapter", "Error validando token: " + e.getMessage(), e);
                return TokenValidationResult.invalid("Invalid token");
            }
        });
    }
}
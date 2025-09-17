package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.auth.TokenAutenticacion;
import co.com.pragma.model.auth.TokenValidationResult;
import co.com.pragma.model.auth.exceptions.AuthenticationException;
import co.com.pragma.model.auth.gateways.AuthenticationGateway;
import co.com.pragma.model.common.gateways.LogGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginAuthenticationUseCaseTest {

    @Mock
    private AuthenticationGateway authenticationGateway;

    @Mock
    private LogGateway logGateway;

    private LoginAuthenticationUseCase loginAuthenticationUseCase;

    @BeforeEach
    void setUp() {
        loginAuthenticationUseCase = new LoginAuthenticationUseCase(authenticationGateway, logGateway);
    }

    @Test
    void login_validCredentials_shouldReturnToken() {
        LoginCredenciales credentials = new LoginCredenciales("test@example.com", "password123");
        TokenAutenticacion expectedToken = new TokenAutenticacion("jwt-token-123");

        when(authenticationGateway.authenticateLogIn(credentials)).thenReturn(Mono.just(expectedToken));

        StepVerifier.create(loginAuthenticationUseCase.login(credentials))
                .expectNext(expectedToken)
                .verifyComplete();

        verify(authenticationGateway).authenticateLogIn(credentials);
        verify(logGateway).info("LoginAuthenticationUseCase", "Iniciando proceso de login para: test@example.com");
        verify(logGateway).info("LoginAuthenticationUseCase", "Login exitoso para: test@example.com");
    }


    @Test
    void validateToken_validToken_shouldReturnValidResult() {
        String token = "valid-jwt-token";
        TokenValidationResult validResult = TokenValidationResult.valid(1, "test@example.com", "ADMIN", "12345678", 1234567890L);

        when(authenticationGateway.validateToken(token)).thenReturn(Mono.just(validResult));

        StepVerifier.create(loginAuthenticationUseCase.validateToken(token))
                .expectNext(validResult)
                .verifyComplete();

        verify(authenticationGateway).validateToken(token);
        verify(logGateway).info("LoginAuthenticationUseCase", "Iniciando validación de token");
        verify(logGateway).info("LoginAuthenticationUseCase", "Token válido para usuario: test@example.com");
    }

    @Test
    void validateToken_invalidToken_shouldReturnInvalidResult() {
        String token = "invalid-jwt-token";
        TokenValidationResult invalidResult = TokenValidationResult.invalid("Token expired");

        when(authenticationGateway.validateToken(token)).thenReturn(Mono.just(invalidResult));

        StepVerifier.create(loginAuthenticationUseCase.validateToken(token))
                .expectNext(invalidResult)
                .verifyComplete();

        verify(authenticationGateway).validateToken(token);
        verify(logGateway).info("LoginAuthenticationUseCase", "Iniciando validación de token");
        verify(logGateway).info("LoginAuthenticationUseCase", "Token inválido: Token expired");
    }



    @Test
    void validateToken_nullEmailInResult_shouldLogCorrectly() {
        String token = "valid-token-with-null-email";
        TokenValidationResult resultWithNullEmail = new TokenValidationResult(true, 1, null, "ADMIN", "12345678", 1234567890L, null);

        when(authenticationGateway.validateToken(token)).thenReturn(Mono.just(resultWithNullEmail));

        StepVerifier.create(loginAuthenticationUseCase.validateToken(token))
                .expectNext(resultWithNullEmail)
                .verifyComplete();

        verify(authenticationGateway).validateToken(token);
        verify(logGateway).info("LoginAuthenticationUseCase", "Iniciando validación de token");
        verify(logGateway).info("LoginAuthenticationUseCase", "Token válido para usuario: null");
    }
}
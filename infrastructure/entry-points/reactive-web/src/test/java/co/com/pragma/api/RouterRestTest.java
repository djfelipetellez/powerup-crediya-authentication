package co.com.pragma.api;

import co.com.pragma.api.config.AuthPath;
import co.com.pragma.api.config.RolPath;
import co.com.pragma.api.config.UsuarioPath;
import co.com.pragma.api.dto.*;
import co.com.pragma.api.mapper.RolMapper;
import co.com.pragma.api.mapper.UsuarioMapper;
import co.com.pragma.api.util.RequestValidator;
import co.com.pragma.model.auth.LoginCredenciales;
import co.com.pragma.model.auth.TokenAutenticacion;
import co.com.pragma.model.auth.TokenValidationResult;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.exceptions.UsuarioNotFoundException;
import co.com.pragma.usecase.auth.LoginAuthenticationUseCase;
import co.com.pragma.usecase.rol.RolUseCase;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    private WebTestClient webTestClient;

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private RolUseCase rolUseCase;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private RolMapper rolMapper;

    @Mock
    private RequestValidator requestValidator;

    @Mock
    private LogGateway logGateway;

    @Mock
    private LoginAuthenticationUseCase loginAuthenticationUseCase;

    @BeforeEach
    void setUp() {
        Handler handler = new Handler(usuarioUseCase, rolUseCase, loginAuthenticationUseCase, usuarioMapper, rolMapper, requestValidator, logGateway);

        UsuarioPath usuarioPath = new UsuarioPath();
        usuarioPath.setBase("/api/v1/usuarios");
        usuarioPath.setValidarExistenciaUsuario("/api/v1/usuarios/validar-existencia/{email}");

        RolPath rolPath = new RolPath();
        rolPath.setRoles("/api/v1/roles");

        AuthPath authPath = new AuthPath();
        authPath.setLogin("/api/v1/auth/login");
        authPath.setValidateToken("/api/v1/auth/validate-token");

        RouterRest routerRest = new RouterRest(usuarioPath, rolPath, authPath);

        // Combinar las RouterFunctions separadas como en tu implementación real
        RouterFunction<ServerResponse> usuarioRoutes = routerRest.usuarioRoutes(handler);
        RouterFunction<ServerResponse> rolRoutes = routerRest.rolRoutes(handler);
        RouterFunction<ServerResponse> authRoutes = routerRest.authRoutes(handler);
        RouterFunction<ServerResponse> validacionRoutes = routerRest.validacionRoutes(handler);
        RouterFunction<ServerResponse> allRoutes = usuarioRoutes.and(rolRoutes).and(authRoutes).and(validacionRoutes);

        webTestClient = WebTestClient
                .bindToRouterFunction(allRoutes)
                .configureClient()
                .baseUrl("http://localhost")
                .build();
    }

    @Test
    void registrarUsuarioTest() {
        // Arrange
        UsuarioRegistroRequestDto requestDto = new UsuarioRegistroRequestDto(
                "test", "test", "test@test.com", "12345", "12345", new BigDecimal(100), "password123", 1);
        Usuario usuario = createUsuarioMock();
        UsuarioResponseDto usuarioResponseDto = createUsuarioResponseDto();

        mockUsuarioFlow(requestDto, usuario, usuarioResponseDto);

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UsuarioResponseDto.class)
                .isEqualTo(usuarioResponseDto);
    }

    @Test
    void registrarRolTest() {
        // Arrange
        RolRegistroRequestDto requestDto = new RolRegistroRequestDto("test", "test");
        Rol rol = createRolMock();
        RoleResponseDto roleResponseDto = createRoleResponseDto();

        mockRolFlow(requestDto, rol, roleResponseDto);

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(RoleResponseDto.class)
                .isEqualTo(roleResponseDto);
    }

    @Test
    void consultarUsuarioTest() {
        // Arrange
        String email = "test@test.com";
        Usuario usuario = Usuario.builder()
                .idUsuario(1)
                .nombre("Test")
                .apellido("User")
                .email(email)
                .documentoIdentidad("123456789")
                .telefono("555-1234")
                .salarioBase(new BigDecimal("50000"))
                .build();
        UsuarioResponseDto responseDto = new UsuarioResponseDto(1, "Test", "User", email, "123456789", "555-1234", new BigDecimal("50000"), new RoleResponseDto(1, "ADMIN", "Administrator"));

        given(usuarioUseCase.consultarUsuario(email))
                .willReturn(Mono.just(usuario));
        given(usuarioMapper.toResponseDto(any(Usuario.class)))
                .willReturn(responseDto);

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/usuarios/validar-existencia/{email}", email)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UsuarioResponseDto.class)
                .isEqualTo(responseDto);
    }

    @Test
    void consultarUsuarioTest_UserNotFound() {
        // Arrange
        String email = "test@test.com";

        given(usuarioUseCase.consultarUsuario(email))
                .willReturn(Mono.error(new UsuarioNotFoundException("Usuario no encontrado")));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/usuarios/validar-existencia/{email}", email)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void loginTest() {
        // Arrange
        LoginRequestDto loginDto = new LoginRequestDto("test@example.com", "password123");
        TokenAutenticacion tokenAuth = new TokenAutenticacion("jwt.token.here");

        given(requestValidator.validate(any(LoginRequestDto.class)))
                .willReturn(Mono.just(loginDto));
        given(loginAuthenticationUseCase.login(any(LoginCredenciales.class)))
                .willReturn(Mono.just(tokenAuth));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(loginDto))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.token").isEqualTo("jwt.token.here")
                .jsonPath("$.message").isEqualTo("Login exitoso");
    }

    @Test
    void validateTokenTest_ValidToken() {
        // Arrange
        TokenValidationRequestDto requestDto = new TokenValidationRequestDto("valid.jwt.token");
        TokenValidationResult validationResult = new TokenValidationResult(
                true, 1, "test@example.com", "ADMIN", "12345678", 1758143042L, null
        );

        given(requestValidator.validate(any(TokenValidationRequestDto.class)))
                .willReturn(Mono.just(requestDto));
        given(loginAuthenticationUseCase.validateToken("valid.jwt.token"))
                .willReturn(Mono.just(validationResult));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/auth/validate-token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.valid").isEqualTo(true)
                .jsonPath("$.userId").isEqualTo(1)
                .jsonPath("$.email").isEqualTo("test@example.com")
                .jsonPath("$.role").isEqualTo("ADMIN")
                .jsonPath("$.documentoIdentidad").isEqualTo("12345678");
    }

    @Test
    void validateTokenTest_InvalidToken() {
        // Arrange
        TokenValidationRequestDto requestDto = new TokenValidationRequestDto("invalid.jwt.token");
        TokenValidationResult validationResult = new TokenValidationResult(
                false, null, null, null, null, null, "Token inválido"
        );

        given(requestValidator.validate(any(TokenValidationRequestDto.class)))
                .willReturn(Mono.just(requestDto));
        given(loginAuthenticationUseCase.validateToken("invalid.jwt.token"))
                .willReturn(Mono.just(validationResult));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/auth/validate-token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.valid").isEqualTo(false)
                .jsonPath("$.error").isEqualTo("Token inválido");
    }

    // Métodos helper para crear objetos mock
    private Usuario createUsuarioMock() {
        return new Usuario();
    }

    private UsuarioResponseDto createUsuarioResponseDto() {
        return new UsuarioResponseDto(
                1, "test", "test", "test@test.com", "12345", "12345", new BigDecimal(100),
                new RoleResponseDto(1, "test", "test"));
    }

    private Rol createRolMock() {
        return new Rol();
    }

    private RoleResponseDto createRoleResponseDto() {
        return new RoleResponseDto(1, "test", "test");
    }

    // Métodos helper para configurar mocks
    private void mockUsuarioFlow(UsuarioRegistroRequestDto requestDto, Usuario usuario, UsuarioResponseDto responseDto) {
        given(requestValidator.validate(any(UsuarioRegistroRequestDto.class)))
                .willReturn(Mono.just(requestDto));
        given(usuarioMapper.toDomain(any(UsuarioRegistroRequestDto.class)))
                .willReturn(usuario);
        given(usuarioUseCase.registrarUsuario(any(Usuario.class), anyInt(), any(String.class)))
                .willReturn(Mono.just(usuario));
        given(usuarioMapper.toResponseDto(any(Usuario.class)))
                .willReturn(responseDto);
    }

    private void mockRolFlow(RolRegistroRequestDto requestDto, Rol rol, RoleResponseDto responseDto) {
        given(requestValidator.validate(any(RolRegistroRequestDto.class)))
                .willReturn(Mono.just(requestDto));
        given(rolMapper.toDomain(any(RolRegistroRequestDto.class)))
                .willReturn(rol);
        given(rolUseCase.registrarRol(any(Rol.class)))
                .willReturn(Mono.just(rol));
        given(rolMapper.toResponseDto(any(Rol.class)))
                .willReturn(responseDto);
    }
}
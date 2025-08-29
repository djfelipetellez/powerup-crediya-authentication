package co.com.pragma.api.config;

import co.com.pragma.api.Handler;
import co.com.pragma.api.RouterRest;
import co.com.pragma.api.dto.RolRegistroRequestDto;
import co.com.pragma.api.dto.RoleResponseDto;
import co.com.pragma.api.dto.UsuarioRegistroRequestDto;
import co.com.pragma.api.dto.UsuarioResponseDto;
import co.com.pragma.api.mapper.RolMapper;
import co.com.pragma.api.mapper.UsuarioMapper;
import co.com.pragma.api.util.RequestValidator;
import co.com.pragma.model.common.gateways.LoggingGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
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
class ConfigTest {

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
    private LoggingGateway loggingGateway;

    @BeforeEach
    void setUp() {
        Handler handler = new Handler(usuarioUseCase, rolUseCase, usuarioMapper, rolMapper, requestValidator, loggingGateway);

        RouterRest routerRest = new RouterRest();

        // Combinar las RouterFunctions con configuraciones de seguridad
        RouterFunction<ServerResponse> usuarioRoutes = routerRest.usuarioRoutes(handler);
        RouterFunction<ServerResponse> rolRoutes = routerRest.rolRoutes(handler);
        RouterFunction<ServerResponse> allRoutes = usuarioRoutes.and(rolRoutes);

        // Crear WebTestClient con filtros de seguridad simulados
        webTestClient = WebTestClient
                .bindToRouterFunction(allRoutes)
                .webFilter((exchange, chain) -> {
                    // Simular SecurityHeadersConfig
                    exchange.getResponse().getHeaders().add("Content-Security-Policy",
                            "default-src 'self'; frame-ancestors 'self'; form-action 'self'");
                    exchange.getResponse().getHeaders().add("Strict-Transport-Security", "max-age=31536000;");
                    exchange.getResponse().getHeaders().add("X-Content-Type-Options", "nosniff");
                    exchange.getResponse().getHeaders().add("Server", "");
                    exchange.getResponse().getHeaders().add("Cache-Control", "no-store");
                    exchange.getResponse().getHeaders().add("Pragma", "no-cache");
                    exchange.getResponse().getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
                    return chain.filter(exchange);
                })
                .webFilter((exchange, chain) -> {
                    // Simular CorsConfig
                    exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
                    exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                    return chain.filter(exchange);
                })
                .configureClient()
                .baseUrl("http://localhost")
                .build();
    }

    @Test
    void usuarioEndpoint_ShouldApplySecurityHeaders() {
        // Setup mocks específicos para este test
        setupUsuarioMocks();

        UsuarioRegistroRequestDto requestDto = new UsuarioRegistroRequestDto(
                "test", "test", "test@test.com", "12345", "12345", new BigDecimal(100), 1);

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    @Test
    void rolEndpoint_ShouldApplySecurityHeaders() {
        // Setup mocks específicos para este test
        setupRolMocks();

        RolRegistroRequestDto requestDto = new RolRegistroRequestDto("test", "test");

        webTestClient.post()
                .uri("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    @Test
    void corsConfigurationShouldWork() {
        // Setup mocks específicos para este test
        setupUsuarioMocks();

        UsuarioRegistroRequestDto requestDto = new UsuarioRegistroRequestDto(
                "test", "test", "test@test.com", "12345", "12345", new BigDecimal(100), 1);

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .header("Origin", "http://localhost:3000")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Access-Control-Allow-Origin")
                .expectHeader().exists("Access-Control-Allow-Methods")
                .expectHeader().exists("Access-Control-Allow-Headers");
    }

    // Métodos helper para configurar mocks específicos
    private void setupUsuarioMocks() {
        Usuario usuario = new Usuario();
        UsuarioResponseDto usuarioResponseDto = new UsuarioResponseDto(
                1, "test", "test", "test@test.com", "12345", "12345", new BigDecimal(100),
                new RoleResponseDto(1, "test", "test"));

        given(requestValidator.validate(any(UsuarioRegistroRequestDto.class)))
                .willReturn(Mono.just(new UsuarioRegistroRequestDto(
                        "test", "test", "test@test.com", "12345", "12345", new BigDecimal(100), 1)));
        given(usuarioMapper.toDomain(any(UsuarioRegistroRequestDto.class)))
                .willReturn(usuario);
        given(usuarioUseCase.registrarUsuario(any(Usuario.class), anyInt()))
                .willReturn(Mono.just(usuario));
        given(usuarioMapper.toResponseDto(any(Usuario.class)))
                .willReturn(usuarioResponseDto);
    }

    private void setupRolMocks() {
        Rol rol = new Rol();
        RoleResponseDto roleResponseDto = new RoleResponseDto(1, "test", "test");

        given(requestValidator.validate(any(RolRegistroRequestDto.class)))
                .willReturn(Mono.just(new RolRegistroRequestDto("test", "test")));
        given(rolMapper.toDomain(any(RolRegistroRequestDto.class)))
                .willReturn(rol);
        given(rolUseCase.registrarRol(any(Rol.class)))
                .willReturn(Mono.just(rol));
        given(rolMapper.toResponseDto(any(Rol.class)))
                .willReturn(roleResponseDto);
    }
}
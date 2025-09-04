package co.com.pragma.api;

import co.com.pragma.api.config.RolPath;
import co.com.pragma.api.config.UsuarioPath;
import co.com.pragma.api.dto.*;
import co.com.pragma.api.mapper.RolMapper;
import co.com.pragma.api.mapper.UsuarioMapper;
import co.com.pragma.api.util.RequestValidator;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.exceptions.UsuarioNotFoundException;
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
    private co.com.pragma.model.common.gateways.LogGateway logGateway;

    @BeforeEach
    void setUp() {
        Handler handler = new Handler(usuarioUseCase, rolUseCase, usuarioMapper, rolMapper, requestValidator, logGateway);

        UsuarioPath usuarioPath = new UsuarioPath();
        usuarioPath.setBase("/api/v1/usuarios");
        usuarioPath.setValidarExistenciaUsuario("/api/v1/usuarios/validar-existencia");
        
        RolPath rolPath = new RolPath();
        rolPath.setRoles("/api/v1/roles");

        RouterRest routerRest = new RouterRest(usuarioPath, rolPath);

        // Combinar las RouterFunctions separadas como en tu implementación real
        RouterFunction<ServerResponse> usuarioRoutes = routerRest.usuarioRoutes(handler);
        RouterFunction<ServerResponse> rolRoutes = routerRest.rolRoutes(handler);
        RouterFunction<ServerResponse> validacionRoutes = routerRest.validacionRoutes(handler);
        RouterFunction<ServerResponse> allRoutes = usuarioRoutes.and(rolRoutes).and(validacionRoutes);

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
                "test", "test", "test@test.com", "12345", "12345", new BigDecimal(100), 1);
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
    void validarExistenciaUsuarioTest() {
        // Arrange
        ClienteValidationRequest requestDto = new ClienteValidationRequest("123456789", "test@test.com");

        given(requestValidator.validate(any(ClienteValidationRequest.class)))
                .willReturn(Mono.just(requestDto));
        given(usuarioUseCase.validarExistenciaUsuario("123456789", "test@test.com"))
                .willReturn(Mono.empty());

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/usuarios/validar-existencia")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void validarExistenciaUsuarioTest_UserNotFound() {
        // Arrange
        ClienteValidationRequest requestDto = new ClienteValidationRequest("123456789", "test@test.com");

        given(requestValidator.validate(any(ClienteValidationRequest.class)))
                .willReturn(Mono.just(requestDto));
        given(usuarioUseCase.validarExistenciaUsuario("123456789", "test@test.com"))
                .willReturn(Mono.error(new UsuarioNotFoundException("Usuario no encontrado")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/usuarios/validar-existencia")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchange()
                .expectStatus().is5xxServerError();
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
        given(usuarioUseCase.registrarUsuario(any(Usuario.class), anyInt()))
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
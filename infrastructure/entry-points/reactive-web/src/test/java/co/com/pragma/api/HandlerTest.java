package co.com.pragma.api;

import co.com.pragma.api.dto.ClienteValidationRequest;
import co.com.pragma.api.dto.RolRegistroRequestDto;
import co.com.pragma.api.dto.UsuarioRegistroRequestDto;
import co.com.pragma.api.dto.UsuarioResponseDto;
import co.com.pragma.api.mapper.RolMapper;
import co.com.pragma.api.mapper.UsuarioMapper;
import co.com.pragma.api.util.RequestValidator;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.usecase.rol.RolUseCase;
import co.com.pragma.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class HandlerTest {

    @InjectMocks
    private Handler handler;

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
    private LogGateway loggingGateway;

    @Mock
    private ServerRequest serverRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarUsuario() {
        UsuarioRegistroRequestDto requestDto = new UsuarioRegistroRequestDto("test", "test", "test@test.com", "123456789", "123456789", new BigDecimal(1000), 1);
        Usuario usuario = new Usuario();
        co.com.pragma.api.dto.RoleResponseDto roleResponseDto = new co.com.pragma.api.dto.RoleResponseDto(1, "test", "test");
        UsuarioResponseDto responseDto = new UsuarioResponseDto(1, "test", "test", "test@test.com", "123456789", "123456789", new BigDecimal(1000), roleResponseDto);

        when(serverRequest.bodyToMono(UsuarioRegistroRequestDto.class)).thenReturn(Mono.just(requestDto));
        when(requestValidator.validate(any())).thenReturn(Mono.just(requestDto));
        when(usuarioMapper.toDomain(any())).thenReturn(usuario);
        when(usuarioUseCase.registrarUsuario(any(), any())).thenReturn(Mono.just(usuario));
        when(usuarioMapper.toResponseDto(any())).thenReturn(responseDto);
        doNothing().when(loggingGateway).info(any(), any());

        handler.registrarUsuario(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void registrarRol() {
        RolRegistroRequestDto requestDto = new RolRegistroRequestDto("test", "test description");
        Rol rol = new Rol();

        when(serverRequest.bodyToMono(RolRegistroRequestDto.class)).thenReturn(Mono.just(requestDto));
        when(requestValidator.validate(any())).thenReturn(Mono.just(requestDto));
        when(rolMapper.toDomain(any())).thenReturn(rol);
        when(rolUseCase.registrarRol(any())).thenReturn(Mono.just(rol));
        when(rolMapper.toResponseDto(any())).thenReturn(new co.com.pragma.api.dto.RoleResponseDto(1, "test", "test description"));
        doNothing().when(loggingGateway).info(any(), any());

        handler.registrarRol(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void validarExistenciaUsuario_Success() {
        ClienteValidationRequest requestDto = new ClienteValidationRequest("123456789", "test@test.com");

        when(serverRequest.bodyToMono(ClienteValidationRequest.class)).thenReturn(Mono.just(requestDto));
        when(requestValidator.validate(any(ClienteValidationRequest.class))).thenReturn(Mono.just(requestDto));
        when(usuarioUseCase.validarExistenciaUsuario(("123456789"), ("test@test.com"))).thenReturn(Mono.empty());
        doNothing().when(loggingGateway).info(any(), any());

        handler.validarExistenciaUsuario(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void validarExistenciaUsuario_UserNotFound() {
        ClienteValidationRequest requestDto = new ClienteValidationRequest("123456789", "test@test.com");

        when(serverRequest.bodyToMono(ClienteValidationRequest.class)).thenReturn(Mono.just(requestDto));
        when(requestValidator.validate(any(ClienteValidationRequest.class))).thenReturn(Mono.just(requestDto));
        when(usuarioUseCase.validarExistenciaUsuario(("123456789"), ("test@test.com")))
                .thenReturn(Mono.error(new IllegalArgumentException("Usuario no encontrado")));
        doNothing().when(loggingGateway).error(any(), any(), any());

        handler.validarExistenciaUsuario(serverRequest)
                .as(StepVerifier::create)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
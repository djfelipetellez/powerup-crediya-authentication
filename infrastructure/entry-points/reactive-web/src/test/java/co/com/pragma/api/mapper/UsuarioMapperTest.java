package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UsuarioRegistroRequestDto;
import co.com.pragma.api.dto.UsuarioResponseDto;
import co.com.pragma.model.usuario.Usuario;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioMapperTest {

    private final UsuarioMapper usuarioMapper = Mappers.getMapper(UsuarioMapper.class);

    @Test
    void toDomain() {
        UsuarioRegistroRequestDto requestDto = new UsuarioRegistroRequestDto(
                "test",
                "test",
                "test@test.com",
                "123456789",
                "123456789",
                new BigDecimal(1000),
                1
        );

        Usuario usuario = usuarioMapper.toDomain(requestDto);

        assertEquals(requestDto.nombre(), usuario.getNombre());
        assertEquals(requestDto.apellido(), usuario.getApellido());
        assertEquals(requestDto.email(), usuario.getEmail());
        assertEquals(requestDto.documentoIdentidad(), usuario.getDocumentoIdentidad());
        assertEquals(requestDto.telefono(), usuario.getTelefono());
        assertEquals(requestDto.salarioBase(), usuario.getSalarioBase());
    }

    @Test
    void toResponseDto() {
        Usuario usuario = Usuario.builder()
                .idUsuario(1)
                .email("test@test.com")
                .build();

        UsuarioResponseDto responseDto = usuarioMapper.toResponseDto(usuario);

        assertEquals(usuario.getIdUsuario(), responseDto.idUsuario());
        assertEquals(usuario.getEmail(), responseDto.email());
    }
}

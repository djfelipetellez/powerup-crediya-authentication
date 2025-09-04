package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RoleResponseDto;
import co.com.pragma.api.dto.UsuarioRegistroRequestDto;
import co.com.pragma.api.dto.UsuarioResponseDto;
import co.com.pragma.model.usuario.Usuario;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioMapperTest {

    private final UsuarioMapper usuarioMapper = new UsuarioMapper() {

        @Override
        public UsuarioResponseDto toResponseDto(Usuario user) {
            if (user == null) return null;

            RoleResponseDto rolDto = null;
            if (user.getRol() != null) {
                rolDto = new RoleResponseDto(
                        user.getRol().getIdRol(),
                        user.getRol().getNombre(),
                        user.getRol().getDescripcion()
                );
            }

            return new UsuarioResponseDto(
                    user.getIdUsuario(),
                    user.getNombre(),
                    user.getApellido(),
                    user.getEmail(),
                    user.getDocumentoIdentidad(),
                    user.getTelefono(),
                    user.getSalarioBase(),
                    rolDto
            );
        }
    };

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
                .nombre("test")
                .apellido("test")
                .email("test@test.com")
                .documentoIdentidad("123456789")
                .telefono("123456789")
                .salarioBase(new BigDecimal(1000))
                .rol(co.com.pragma.model.rol.Rol.builder()
                        .idRol(1)
                        .nombre("ADMIN")
                        .descripcion("Administrador")
                        .build())
                .build();

        UsuarioResponseDto responseDto = usuarioMapper.toResponseDto(usuario);

        assertEquals(usuario.getIdUsuario(), responseDto.idUsuario());
        assertEquals(usuario.getNombre(), responseDto.nombre());
        assertEquals(usuario.getApellido(), responseDto.apellido());
        assertEquals(usuario.getEmail(), responseDto.email());
        assertEquals(usuario.getDocumentoIdentidad(), responseDto.documentoIdentidad());
        assertEquals(usuario.getTelefono(), responseDto.telefono());
        assertEquals(usuario.getSalarioBase(), responseDto.salarioBase());
        assertEquals(usuario.getRol().getIdRol(), responseDto.rol().idRol());
        assertEquals(usuario.getRol().getNombre(), responseDto.rol().nombre());
        assertEquals(usuario.getRol().getDescripcion(), responseDto.rol().descripcion());
    }
}

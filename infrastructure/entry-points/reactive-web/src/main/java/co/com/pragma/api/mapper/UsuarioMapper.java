package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RoleResponseDto;
import co.com.pragma.api.dto.UsuarioRegistroRequestDto;
import co.com.pragma.api.dto.UsuarioResponseDto;
import co.com.pragma.model.usuario.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RolMapper.class})
public interface UsuarioMapper {

    default Usuario toDomain(UsuarioRegistroRequestDto requestDto) {
        return Usuario.builder()
                .nombre(requestDto.nombre())
                .apellido(requestDto.apellido())
                .email(requestDto.email())
                .documentoIdentidad(requestDto.documentoIdentidad())
                .telefono(requestDto.telefono())
                .salarioBase(requestDto.salarioBase())
                .build();
    }

    default UsuarioResponseDto toResponseDto(Usuario user) {
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
}
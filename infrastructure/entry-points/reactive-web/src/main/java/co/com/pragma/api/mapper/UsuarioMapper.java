package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UsuarioRegistroRequestDto;
import co.com.pragma.api.dto.UsuarioResponseDto;
import co.com.pragma.model.usuario.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
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

    UsuarioResponseDto toResponseDto(Usuario user);
}
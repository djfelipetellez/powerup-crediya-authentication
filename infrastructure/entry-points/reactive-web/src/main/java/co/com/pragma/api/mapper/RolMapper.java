package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RolRegistroRequestDto;
import co.com.pragma.api.dto.RoleResponseDto;
import co.com.pragma.model.rol.Rol;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolMapper {

    @Mapping(target = "idRol", ignore = true)
    Rol toDomain(RolRegistroRequestDto rolRegistroRequestDto);

    @Mapping(source = "idRol", target = "idRol")
    RoleResponseDto toResponseDto(Rol rol);
}

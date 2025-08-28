package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RolRegistroRequestDto;
import co.com.pragma.api.dto.RoleResponseDto;
import co.com.pragma.model.rol.Rol;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RolMapperTest {

    private final RolMapper rolMapper = Mappers.getMapper(RolMapper.class);

    @Test
    void toDomain() {
        RolRegistroRequestDto requestDto = new RolRegistroRequestDto("test", "test description");

        Rol rol = rolMapper.toDomain(requestDto);

        assertEquals(requestDto.nombre(), rol.getNombre());
    }

    @Test
    void toResponseDto() {
        Rol rol = Rol.builder()
                .idRol(1)
                .nombre("test")
                .build();

        RoleResponseDto responseDto = rolMapper.toResponseDto(rol);

        assertEquals(rol.getIdRol(), responseDto.idRol());
        assertEquals(rol.getNombre(), responseDto.nombre());
    }
}

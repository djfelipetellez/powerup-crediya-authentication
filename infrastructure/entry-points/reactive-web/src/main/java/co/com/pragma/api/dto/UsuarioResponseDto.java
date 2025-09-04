package co.com.pragma.api.dto;

import java.math.BigDecimal;

public record UsuarioResponseDto(
        Integer idUsuario,
        String nombre,
        String apellido,
        String email,
        String documentoIdentidad,
        String telefono,
        BigDecimal salarioBase,
        RoleResponseDto rol
) {
}

package co.com.pragma.api.dto;

public record UsuarioResponseDto(
        Integer idUsuario,
        String nombre,
        String apellido,
        String email,
        String documentoIdentidad,
        String telefono,
        Double salarioBase,
        RoleResponseDto rol
) {
}

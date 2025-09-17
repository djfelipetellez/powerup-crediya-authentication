package co.com.pragma.api.dto;

public record TokenValidationResponseDto(
        boolean valid,
        Integer userId,
        String email,
        String role,
        String documentoIdentidad,
        Long exp,
        String error
) {
}
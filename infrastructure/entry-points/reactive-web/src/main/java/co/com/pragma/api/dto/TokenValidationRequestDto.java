package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenValidationRequestDto(@NotBlank(message = "El token es obligatorio") String token) {
}
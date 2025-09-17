package co.com.pragma.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(@Email @NotBlank(message = "El correo es obligatorio") String email,
                              @NotBlank(message = "La contraseña es obligatoria") String password
) {
}
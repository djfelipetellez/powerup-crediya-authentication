package co.com.pragma.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRegistroRequestDto(@NotBlank(message = "El nombre es obligatorio") String nombre,
                                        @NotBlank(message = "El apellido es obligatorio") String apellido,
                                        @Email @NotBlank(message = "El correo es obligatorio") String email,
                                        @NotBlank(message = "El documento de identidad es obligatorio") String documentoIdentidad,
                                        String telefono,
                                        @NotNull(message = "El salario base es obligatorio") Double salarioBase,
                                        // Remove salary validation
                                        @NotNull(message = "El ID del rol es obligatorio") Integer idRol) {

}
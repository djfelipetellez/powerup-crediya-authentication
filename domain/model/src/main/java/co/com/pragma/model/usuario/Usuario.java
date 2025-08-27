package co.com.pragma.model.usuario;

import co.com.pragma.model.rol.Rol;
import lombok.*;
// Remove validation imports
// import jakarta.validation.constraints.NotBlank;


@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class Usuario {

    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String documentoIdentidad;
    private String telefono;
    private Double salarioBase;
    private Rol rol;


}
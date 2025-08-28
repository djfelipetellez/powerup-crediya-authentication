package co.com.pragma.model.usuario;

import co.com.pragma.model.rol.Rol;
import lombok.*;

import java.math.BigDecimal;

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
    private BigDecimal salarioBase;
    private Rol rol;


}
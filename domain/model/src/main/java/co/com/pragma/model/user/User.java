package co.com.pragma.model.user;
import co.com.pragma.model.role.Role;
import lombok.*;
//import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class User {

    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String documentoIdentidad;
    private String telefono;
    private Double salarioBase;
    private Role rol;

    public boolean tieneRol(String nombreRol) {
        return rol != null && rol.getNombre().equalsIgnoreCase(nombreRol);
    }

}
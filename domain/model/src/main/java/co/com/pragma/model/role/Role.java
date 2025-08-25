package co.com.pragma.model.role;

import lombok.*;
//import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Role {

    private Integer id;
    private String nombre;
    private String descripcion;

    // Ejemplo de regla de dominio
    public boolean esAdministrador() {
        return "ADMIN".equalsIgnoreCase(this.nombre);
    }
}

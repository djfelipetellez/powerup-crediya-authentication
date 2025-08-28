package co.com.pragma.model.rol;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Rol {

    private Integer idRol;
    private String nombre;
    private String descripcion;

}

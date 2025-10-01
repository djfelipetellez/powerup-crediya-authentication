package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_schema.rol")
public class RolEntity {

    @Id
    private Integer idRol;

    private String nombre;
    private String descripcion;
}
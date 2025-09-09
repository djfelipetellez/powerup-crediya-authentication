package co.com.pragma.model.auth;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class UsuarioCredencial {

    private Integer id;
    private String email;
    private String password;
    private Integer idUsuario;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private boolean active;

}
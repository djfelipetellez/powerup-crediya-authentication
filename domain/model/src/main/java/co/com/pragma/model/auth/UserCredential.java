package co.com.pragma.model.auth;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class UserCredential {

    private Integer id;
    private String email;
    private String password;
    private Integer usuarioId;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private boolean active;

}
package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("user_credentials")
public class UserCredentialEntity {

    @Id
    private Integer id;
    private String email;
    private String password;
    private Integer usuarioId;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private boolean active;

}
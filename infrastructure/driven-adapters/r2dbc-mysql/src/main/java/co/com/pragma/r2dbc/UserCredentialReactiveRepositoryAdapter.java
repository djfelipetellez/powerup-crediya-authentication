package co.com.pragma.r2dbc;

import co.com.pragma.model.auth.UserCredential;
import co.com.pragma.model.auth.gateways.UserCredencialRepository;
import co.com.pragma.r2dbc.entity.UserCredentialEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserCredentialReactiveRepositoryAdapter implements UserCredencialRepository {

    private final UserCredentialReactiveRepository repository;

    @Override
    public Mono<UserCredential> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Mono<UserCredential> save(UserCredential userCredential) {
        UserCredentialEntity entity = toEntity(userCredential);
        return repository.save(entity)
                .map(this::toDomain);
    }

    @Override
    public Mono<UserCredential> updateLastLogin(Integer id) {
        return repository.updateLastLoginById(id)
                .map(this::toDomain);
    }

    private UserCredential toDomain(UserCredentialEntity entity) {
        return UserCredential.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .usuarioId(entity.getUsuarioId())
                .createdAt(entity.getCreatedAt())
                .lastLoginAt(entity.getLastLoginAt())
                .active(entity.isActive())
                .build();
    }

    private UserCredentialEntity toEntity(UserCredential domain) {
        return UserCredentialEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .usuarioId(domain.getUsuarioId())
                .createdAt(domain.getCreatedAt())
                .lastLoginAt(domain.getLastLoginAt())
                .active(domain.isActive())
                .build();
    }

}
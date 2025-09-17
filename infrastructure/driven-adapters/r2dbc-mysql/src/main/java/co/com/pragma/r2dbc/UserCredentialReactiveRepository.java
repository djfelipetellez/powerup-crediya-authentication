package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.UserCredentialEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserCredentialReactiveRepository extends ReactiveCrudRepository<UserCredentialEntity, Integer>, ReactiveQueryByExampleExecutor<UserCredentialEntity> {

    Mono<UserCredentialEntity> findByEmail(String email);

    @Query("UPDATE user_credentials SET last_login_at = NOW() WHERE id = :id RETURNING *")
    Mono<UserCredentialEntity> updateLastLoginById(Integer id);

}
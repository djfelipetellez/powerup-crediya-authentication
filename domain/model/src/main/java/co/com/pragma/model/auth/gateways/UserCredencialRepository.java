package co.com.pragma.model.auth.gateways;

import co.com.pragma.model.auth.UserCredential;
import reactor.core.publisher.Mono;

public interface UserCredencialRepository {

    Mono<UserCredential> findByEmail(String email);
    Mono<UserCredential> save(UserCredential userCredential);
    Mono<UserCredential> updateLastLogin(Integer id);

}
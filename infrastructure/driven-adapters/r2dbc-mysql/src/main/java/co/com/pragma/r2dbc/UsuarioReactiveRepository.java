package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.UsuarioEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioEntity, Integer>, ReactiveQueryByExampleExecutor<UsuarioEntity> {

    Mono<UsuarioEntity> findByEmail(String email);

    Mono<UsuarioEntity> findByDocumentoIdentidadAndEmail(String documentoIdentidad, String email);

}

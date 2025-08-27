package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.RolEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolEntity, Integer>, ReactiveQueryByExampleExecutor<RolEntity> { // Add ReactiveQueryByExampleExecutor
}
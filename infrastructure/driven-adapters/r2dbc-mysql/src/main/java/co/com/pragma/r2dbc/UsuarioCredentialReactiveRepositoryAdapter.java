package co.com.pragma.r2dbc;

import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.r2dbc.entity.UserCredentialEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UsuarioCredentialReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        UsuarioCredencial,
        UserCredentialEntity,
        Integer,
        UserCredentialReactiveRepository
        > implements UsuarioCredencialRepository {

    public UsuarioCredentialReactiveRepositoryAdapter(UserCredentialReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, UsuarioCredencial.class));
    }

    @Override
    public Mono<UsuarioCredencial> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toEntity);
    }

    @Override
    public Mono<UsuarioCredencial> save(UsuarioCredencial usuarioCredencial) {
        UserCredentialEntity entity = toData(usuarioCredencial);
        return repository.save(entity)
                .map(this::toEntity);
    }

    @Override
    public Mono<UsuarioCredencial> updateLastLogin(Integer id) {
        return repository.updateLastLoginById(id)
                .map(this::toEntity);
    }

}
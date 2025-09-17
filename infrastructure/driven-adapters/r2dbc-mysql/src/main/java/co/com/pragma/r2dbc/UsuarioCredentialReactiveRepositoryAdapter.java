package co.com.pragma.r2dbc;

import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.r2dbc.entity.UserCredentialEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UsuarioCredentialReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        UsuarioCredencial,
        UserCredentialEntity,
        Integer,
        UserCredentialReactiveRepository
        > implements UsuarioCredencialRepository {

    private final PasswordEncoder passwordEncoder;
    private final LogGateway logGateway;

    public UsuarioCredentialReactiveRepositoryAdapter(UserCredentialReactiveRepository repository, ObjectMapper mapper, PasswordEncoder passwordEncoder, LogGateway logGateway) {
        super(repository, mapper, d -> mapper.map(d, UsuarioCredencial.class));
        this.passwordEncoder = passwordEncoder;
        this.logGateway = logGateway;
    }

    @Override
    public Mono<UsuarioCredencial> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toEntity);
    }

    @Override
    public Mono<UsuarioCredencial> save(UsuarioCredencial usuarioCredencial) {
        logGateway.debug("UsuarioCredentialAdapter", "Guardando credenciales para usuario: " + usuarioCredencial.getEmail());

        // Hash de la contraseña antes de guardar
        String hashedPassword = passwordEncoder.encode(usuarioCredencial.getPassword());

        UsuarioCredencial credentialWithHashedPassword = UsuarioCredencial.builder()
                .email(usuarioCredencial.getEmail())
                .password(hashedPassword)
                .idUsuario(usuarioCredencial.getIdUsuario())
                .createdAt(usuarioCredencial.getCreatedAt())
                .active(usuarioCredencial.isActive())
                .build();

        UserCredentialEntity entity = toData(credentialWithHashedPassword);
        return repository.save(entity)
                .doOnSuccess(savedEntity ->
                        logGateway.debug("UsuarioCredentialAdapter", "Credenciales guardadas exitosamente para: " + usuarioCredencial.getEmail()))
                .doOnError(error ->
                        logGateway.error("UsuarioCredentialAdapter", "Error guardando credenciales: " + error.getMessage(), error))
                .map(this::toEntity);
    }

    @Override
    public Mono<UsuarioCredencial> updateLastLogin(Integer id) {
        return repository.updateLastLoginById(id)
                .map(this::toEntity);
    }

}
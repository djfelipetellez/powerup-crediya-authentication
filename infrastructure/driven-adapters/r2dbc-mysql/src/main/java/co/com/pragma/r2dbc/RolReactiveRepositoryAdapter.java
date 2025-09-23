package co.com.pragma.r2dbc;

import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.r2dbc.entity.RolEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol,
        RolEntity,
        Integer,
        RolReactiveRepository> implements RolRepository {

    private final LogGateway logGateway;

    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper, LogGateway logGateway) {
        super(repository, mapper, d -> mapper.map(d, Rol.class));
        this.logGateway = logGateway;
    }

    @Override
    public Mono<Rol> findById(Integer id) {
        logGateway.debug("RolRepositoryAdapter", "Buscando rol por ID: " + id);

        return repository.findById(id)
                .doOnSuccess(entity ->
                        logGateway.debug("RolRepositoryAdapter", "Rol encontrado: " + (entity != null ? entity.getNombre() : "null")))
                .doOnError(error ->
                        logGateway.error("RolRepositoryAdapter", "Error buscando rol por ID: " + error.getMessage(), error))
                .map(entity -> Rol.builder()
                        .idRol(entity.getIdRol())
                        .nombre(entity.getNombre())
                        .descripcion(entity.getDescripcion())
                        .build()
                );
    }

    @Override
    public Mono<Rol> save(Rol rol) {
        logGateway.debug("RolRepositoryAdapter", "Guardando rol: " + rol.getNombre());

        RolEntity rolEntity = new RolEntity();
        rolEntity.setNombre(rol.getNombre());
        rolEntity.setDescripcion(rol.getDescripcion());

        return repository.save(rolEntity)
                .doOnSuccess(savedEntity ->
                        logGateway.debug("RolRepositoryAdapter", "Rol guardado exitosamente: " + savedEntity.getNombre()))
                .doOnError(error ->
                        logGateway.error("RolRepositoryAdapter", "Error guardando rol: " + error.getMessage(), error))
                .map(savedEntity -> Rol.builder()
                        .idRol(savedEntity.getIdRol())
                        .nombre(savedEntity.getNombre())
                        .descripcion(savedEntity.getDescripcion())
                        .build()
                );
    }
}
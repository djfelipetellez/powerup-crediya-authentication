package co.com.pragma.r2dbc;

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


    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Rol.class));
        this.mapper = mapper;
    }

    @Override
    public Mono<Rol> findById(Integer id) {
        return repository.findById(id)
                .map(entity -> Rol.builder()
                        .idRol(entity.getIdRol())
                        .nombre(entity.getNombre())
                        .descripcion(entity.getDescripcion())
                        .build()
                );
    }

    @Override
    public Mono<Rol> save(Rol rol) {
        RolEntity rolEntity = new RolEntity();
        rolEntity.setNombre(rol.getNombre());
        rolEntity.setDescripcion(rol.getDescripcion());

        return repository.save(rolEntity)
                .map(savedEntity -> Rol.builder()
                        .idRol(savedEntity.getIdRol())
                        .nombre(savedEntity.getNombre())
                        .descripcion(savedEntity.getDescripcion())
                        .build()
                );
    }
}
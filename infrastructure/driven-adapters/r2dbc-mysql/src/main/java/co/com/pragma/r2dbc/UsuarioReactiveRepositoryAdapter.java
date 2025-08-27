package co.com.pragma.r2dbc;

import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.r2dbc.entity.UsuarioEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UsuarioReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Usuario,
        UsuarioEntity,
        Integer,
        UsuarioReactiveRepository
        > implements UsuarioRepository {

    private final UsuarioReactiveRepository repository;
    private final RolReactiveRepository rolRepository;

    public UsuarioReactiveRepositoryAdapter(UsuarioReactiveRepository repository, RolReactiveRepository rolRepository
            , ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Usuario.class));
        this.repository = repository;
        this.rolRepository = rolRepository;
    }

    @Override
    public Mono<Usuario> save(Usuario usuario) {
        // Debug: Verificar qué está llegando al repository
        System.out.println("=== DEBUG REPOSITORY ===");
        System.out.println("Usuario: " + (usuario != null ? usuario.getNombre() : "null"));
        System.out.println("Rol: " + (usuario != null && usuario.getRol() != null ? usuario.getRol() : "null"));
        System.out.println("ID Rol: " + (usuario != null && usuario.getRol() != null ? usuario.getRol().getIdRol() : "null"));
        System.out.println("========================");

        // Validar que el usuario tenga un rol asignado
        if (usuario.getRol() == null || usuario.getRol().getIdRol() == null) {
            return Mono.error(new IllegalArgumentException("El usuario debe tener un rol asignado - Rol: " +
                    (usuario.getRol() != null ? usuario.getRol() : "null")));
        }

        // Crear la entidad para guardar (tu UseCase ya validó que el rol existe)
        UsuarioEntity entity = UsuarioEntity.builder()
                .idUsuario(usuario.getIdUsuario()) // null si es nuevo
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .documentoIdentidad(usuario.getDocumentoIdentidad())
                .telefono(usuario.getTelefono())
                .salarioBase(usuario.getSalarioBase())
                .idRol(usuario.getRol().getIdRol()) // Ya validado en UseCase
                .build();

        // Guardar y retornar el usuario manteniendo el rol que ya viene completo del UseCase
        return repository.save(entity)
                .map(savedEntity -> Usuario.builder()
                        .idUsuario(savedEntity.getIdUsuario())
                        .nombre(savedEntity.getNombre())
                        .apellido(savedEntity.getApellido())
                        .email(savedEntity.getEmail())
                        .documentoIdentidad(savedEntity.getDocumentoIdentidad())
                        .telefono(savedEntity.getTelefono())
                        .salarioBase(savedEntity.getSalarioBase())
                        .rol(usuario.getRol()) // Mantener el rol completo que ya viene del UseCase
                        .build()
                );
    }

    @Override
    public Mono<Usuario> findByEmail(String email) {
        return repository.findByEmail(email)
                .flatMap(entity -> {
                    if (entity.getIdRol() != null) {
                        return rolRepository.findById(entity.getIdRol())
                                .map(rolEntity -> Usuario.builder()
                                        .idUsuario(entity.getIdUsuario())
                                        .nombre(entity.getNombre())
                                        .apellido(entity.getApellido())
                                        .email(entity.getEmail())
                                        .documentoIdentidad(entity.getDocumentoIdentidad())
                                        .telefono(entity.getTelefono())
                                        .salarioBase(entity.getSalarioBase())
                                        .rol(Rol.builder()
                                                .idRol(rolEntity.getIdRol())
                                                .nombre(rolEntity.getNombre())
                                                .descripcion(rolEntity.getDescripcion())
                                                .build())
                                        .build()
                                );
                    } else {
                        // Esto no debería pasar según tu lógica de negocio, pero por seguridad
                        return Mono.error(new IllegalStateException("Usuario encontrado sin rol asignado"));
                    }
                });
    }
}
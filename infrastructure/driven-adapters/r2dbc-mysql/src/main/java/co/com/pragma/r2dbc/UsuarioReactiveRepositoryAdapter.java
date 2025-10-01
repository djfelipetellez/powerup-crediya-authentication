package co.com.pragma.r2dbc;

import co.com.pragma.model.auth.UsuarioCredencial;
import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.r2dbc.entity.UsuarioEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public class UsuarioReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Usuario,
        UsuarioEntity,
        Integer,
        UsuarioReactiveRepository
        > implements UsuarioRepository {

    private final RolRepository rolRepository;
    private final UsuarioCredencialRepository usuarioCredencialRepository;
    private final LogGateway logGateway;

    public UsuarioReactiveRepositoryAdapter(UsuarioReactiveRepository repository,
                                            RolRepository rolRepository,
                                            UsuarioCredencialRepository usuarioCredencialRepository,
                                            ObjectMapper mapper,
                                            LogGateway logGateway) {
        super(repository, mapper, UsuarioReactiveRepositoryAdapter::buildUsuario);
        this.rolRepository = rolRepository;
        this.usuarioCredencialRepository = usuarioCredencialRepository;
        this.logGateway = logGateway;
    }

    private static Usuario buildUsuario(UsuarioEntity entity) {
        return Usuario.builder()
                .idUsuario(entity.getIdUsuario())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .email(entity.getEmail())
                .documentoIdentidad(entity.getDocumentoIdentidad())
                .telefono(entity.getTelefono())
                .salarioBase(entity.getSalarioBase())
                .build();
    }

    @Override
    protected UsuarioEntity toData(Usuario entity) {
        UsuarioEntity usuarioEntity = mapper.map(entity, UsuarioEntity.class);

        // Mapeo manual del rol al idRol
        if (entity.getRol() != null && entity.getRol().getIdRol() != null) {
            usuarioEntity.setIdRol(entity.getRol().getIdRol());
        }

        return usuarioEntity;
    }

    @Override
    public Mono<Usuario> save(Usuario usuario) {
        return validateUsuarioMono(usuario)
                .then(Mono.defer(() -> {
                    logGateway.debug("UsuarioRepositoryAdapter", "Guardando usuario: " + usuario.getEmail());
                    return super.save(usuario);
                }))
                .doOnSuccess(saved -> logGateway.debug("UsuarioRepositoryAdapter", "Usuario guardado: " + saved.getEmail()))
                .doOnError(error -> logGateway.error("UsuarioRepositoryAdapter", "Error guardando usuario: " + error.getMessage(), error));
    }

    @Override
    public Mono<Usuario> findById(Integer id) {
        return repository.findById(id)
                .flatMap(this::enrichWithRoleFromEntity);
    }

    @Override
    public Mono<Usuario> findByEmail(String email) {
        if (email == null) return Mono.empty();

        return repository.findByEmail(email)
                .flatMap(this::enrichWithRoleFromEntity);
    }

    @Override
    @Transactional
    public Mono<Usuario> registrarUsuarioCompleto(Usuario usuario, Integer roleId, String password) {
        logGateway.info("UsuarioRepositoryAdapter", "Registrando usuario completo con credenciales: " + usuario.getEmail());

        return rolRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Rol no encontrado con ID: " + roleId)))
                .flatMap(rol -> {
                    Usuario usuarioConRol = usuario.toBuilder().rol(rol).build();
                    return this.save(usuarioConRol)
                            .flatMap(saved -> this.findById(saved.getIdUsuario())); // Usar findById para obtener usuario completo con rol
                })
                .flatMap(usuarioRegistrado -> crearCredencialesParaUsuario(usuarioRegistrado, password)
                        .thenReturn(usuarioRegistrado)
                )
                .doOnSuccess(saved -> logGateway.info("UsuarioRepositoryAdapter", "Usuario y credenciales registrados exitosamente: " + saved.getEmail()))
                .doOnError(error -> logGateway.error("UsuarioRepositoryAdapter", "Error registrando usuario completo: " + error.getMessage(), error));
    }

    private Mono<UsuarioCredencial> crearCredencialesParaUsuario(Usuario usuario, String password) {
        UsuarioCredencial credential = UsuarioCredencial.builder()
                .email(usuario.getEmail())
                .password(password)
                .idUsuario(usuario.getIdUsuario())
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        return usuarioCredencialRepository.save(credential);
    }


    private Mono<Void> validateUsuarioMono(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getIdRol() == null) {
            return Mono.error(new IllegalArgumentException("El usuario debe tener un rol asignado"));
        }
        return Mono.empty();
    }

    private Mono<Usuario> enrichWithRoleFromEntity(UsuarioEntity entity) {
        if (entity == null) return Mono.empty();

        Usuario usuario = toEntity(entity);

        if (entity.getIdRol() == null) {
            logGateway.info("UsuarioRepositoryAdapter", "Usuario sin idRol asignado: " + usuario.getEmail());
            return Mono.error(new IllegalStateException("Usuario encontrado sin rol asignado"));
        }

        logGateway.debug("UsuarioRepositoryAdapter", "Buscando rol con ID: " + entity.getIdRol() + " para usuario: " + usuario.getEmail());
        return rolRepository.findById(entity.getIdRol())
                .doOnNext(rol -> logGateway.debug("UsuarioRepositoryAdapter", "Rol encontrado: " + rol.getNombre() + " para usuario: " + usuario.getEmail()))
                .doOnSuccess(rol -> {
                    if (rol == null) {
                        logGateway.info("UsuarioRepositoryAdapter", "ROL NO ENCONTRADO con ID: " + entity.getIdRol() + " para usuario: " + usuario.getEmail());
                    }
                })
                .map(rol -> usuario.toBuilder().rol(rol).build())
                .switchIfEmpty(Mono.error(new IllegalStateException("Usuario encontrado sin rol asignado")));
    }

}
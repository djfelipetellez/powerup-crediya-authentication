package co.com.pragma.usecase.usuario;

import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.usuario.Usuario;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    private static final double SALARIO_MINIMO = 0;
    private static final double SALARIO_MAXIMO = 15000000;

    public Mono<Usuario> registrarUsuario(Usuario usuario, Integer roleId) {
        System.out.println("=== DEBUG USECASE ===");
        System.out.println("Usuario inicial: " + usuario.getNombre());
        System.out.println("RoleId recibido: " + roleId);
        System.out.println("Usuario rol inicial: " + usuario.getRol());

        return validarSalario(usuario)
                .then(rolRepository.findById(roleId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Rol no encontrado")))
                        .flatMap(rol -> {
                            System.out.println("Rol encontrado: " + rol.getNombre() + " ID: " + rol.getIdRol());
                            usuario.setRol(rol);
                            System.out.println("Usuario después de setRol: " + usuario.getRol());
                            return Mono.just(usuario);
                        }))
                .flatMap(userWithRol -> {
                    System.out.println("Usuario antes de save: " + userWithRol.getRol());
                    return usuarioRepository.findByEmail(userWithRol.getEmail())
                            .flatMap(existingUser -> Mono.<Usuario>error(new IllegalArgumentException("El correo ya está registrado")))
                            .switchIfEmpty(usuarioRepository.save(userWithRol));
                });
    }

    private Mono<Void> validarSalario(Usuario usuario) {
        if (usuario.getSalarioBase() == null || usuario.getSalarioBase() < SALARIO_MINIMO || usuario.getSalarioBase() > SALARIO_MAXIMO) {
            return Mono.error(new IllegalArgumentException("El salario base debe estar entre " + SALARIO_MINIMO + " y " + SALARIO_MAXIMO));
        }

        return Mono.empty();
    }
}
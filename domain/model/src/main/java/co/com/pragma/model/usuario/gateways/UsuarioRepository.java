package co.com.pragma.model.usuario.gateways;

import co.com.pragma.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {

    Mono<Usuario> save(Usuario usuario);

    Mono<Usuario> findByEmail(String email);

    Mono<Usuario> findByDocumentoIdentidadAndEmail(String documentoIdentidad, String email);

    Mono<Usuario> registrarUsuarioCompleto(Usuario usuario, Integer roleId);
}

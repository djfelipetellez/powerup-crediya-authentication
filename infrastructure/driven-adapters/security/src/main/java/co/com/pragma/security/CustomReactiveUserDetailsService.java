package co.com.pragma.security;

import co.com.pragma.model.auth.gateways.UsuarioCredencialRepository;
import co.com.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UsuarioCredencialRepository userCredencialRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userCredencialRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Usuario no encontrado con email: " + email)))
                .flatMap(usuarioCredencial ->
                        usuarioRepository.findById(usuarioCredencial.getIdUsuario())
                                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Datos de usuario no encontrados para email: " + email)))
                                .map(usuario -> new CustomUserDetails(usuarioCredencial, usuario))
                )
                .cast(UserDetails.class);
    }
}
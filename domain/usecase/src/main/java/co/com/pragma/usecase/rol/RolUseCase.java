package co.com.pragma.usecase.rol;

import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.rol.gateways.RolRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RolUseCase {

    private final RolRepository rolRepository;

    public Mono<Rol> registrarRol(Rol rol) {
        return rolRepository.save(rol);
    }
}

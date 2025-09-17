package co.com.pragma.usecase.rol;

import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.rol.gateways.RolRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RolUseCase {

    private final RolRepository rolRepository;
    private final LogGateway logGateway;

    public Mono<Rol> registrarRol(Rol rol) {
        logGateway.info("RolUseCase", "Iniciando registro de rol: " + (rol != null ? rol.getNombre() : "null"));

        return rolRepository.save(rol)
                .doOnSuccess(rolRegistrado ->
                        logGateway.info("RolUseCase", "Rol registrado exitosamente: " + rolRegistrado.getNombre())
                )
                .doOnError(error ->
                        logGateway.error("RolUseCase", "Error al registrar rol: " + error.getMessage(), error)
                );
    }

}

package co.com.pragma.usecase.rol;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.gateways.LoggingGateway;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.rol.gateways.RolValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RolUseCase implements RolValidator {

    private final RolRepository rolRepository;
    private final LoggingGateway loggingGateway;

    public Mono<Rol> registrarRol(Rol rol) {
        loggingGateway.info("RolUseCase", "Iniciando registro de rol: " + (rol != null ? rol.getNombre() : "null"));

        return rolRepository.save(rol)
                .doOnSuccess(rolRegistrado ->
                        loggingGateway.info("RolUseCase", "Rol registrado exitosamente: " + rolRegistrado.getNombre())
                )
                .doOnError(error ->
                        loggingGateway.error("RolUseCase", "Error al registrar rol: " + error.getMessage(), error)
                );
    }

    public Mono<Void> validarRolExiste(Integer roleId) {
        loggingGateway.info("RolUseCase", "Validando existencia de rol con ID: " + roleId);

        return rolRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constantes.MSG_ROLE_NOT_EXISTS)))
                .doOnSuccess(rol ->
                        loggingGateway.info("RolUseCase", "Rol encontrado: " + rol.getNombre())
                )
                .doOnError(error ->
                        loggingGateway.warn("RolUseCase", "Rol no encontrado con ID: " + roleId, new IllegalArgumentException(Constantes.MSG_ROLE_NOT_EXISTS))
                )
                .then();
    }
}

package co.com.pragma.usecase.rol;

import co.com.pragma.model.common.Constantes;
import co.com.pragma.model.common.LoggingUtil;
import co.com.pragma.model.rol.Rol;
import co.com.pragma.model.rol.gateways.RolRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RolUseCase {

    private final RolRepository rolRepository;

    public Mono<Rol> registrarRol(Rol rol) {
        LoggingUtil.info("RolUseCase", "Iniciando registro de rol: " + (rol != null ? rol.getNombre() : "null"));

        return rolRepository.save(rol)
                .doOnSuccess(rolRegistrado ->
                        LoggingUtil.info("RolUseCase", "Rol registrado exitosamente: " + rolRegistrado.getNombre())
                )
                .doOnError(error ->
                        LoggingUtil.error("RolUseCase", "Error al registrar rol: " + error.getMessage(), error)
                );
    }

    public Mono<Void> validarRolExiste(Integer roleId) {
        LoggingUtil.info("RolUseCase", "Validando existencia de rol con ID: " + roleId);

        return rolRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(Constantes.MSG_ROLE_NOT_EXISTS)))
                .doOnSuccess(rol ->
                        LoggingUtil.info("RolUseCase", "Rol encontrado: " + rol.getNombre())
                )
                .doOnError(error ->
                        LoggingUtil.warn("RolUseCase", "Rol no encontrado con ID: " + roleId, new IllegalArgumentException(Constantes.MSG_ROLE_NOT_EXISTS))
                )
                .then();
    }
}

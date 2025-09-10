package co.com.pragma.api.exception;

import co.com.pragma.api.util.ApiConstantes;
import co.com.pragma.model.auth.exceptions.AuthenticationException;
import co.com.pragma.model.common.exceptions.BusinessRuleException;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.usuario.exceptions.UsuarioNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final LogGateway logGateway;

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext,
                                  ServerCodecConfigurer serverCodecConfigurer, LogGateway logGateway) {
        super(errorAttributes, new WebProperties().getResources(), applicationContext);
        this.logGateway = logGateway;
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {
        Throwable error = getError(request);
        String action = ApiConstantes.LOG_GLOBAL_EXCEPTION_HANDLER;
        final HttpStatus httpStatus;
        final ProblemDetail problemDetail;

        switch (error) {
            case ConstraintViolationException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                logGateway.warn(action, ApiConstantes.CONSTRAINT_VIOLATION + ex.getMessage(), ex);
                List<String> constraintErrors = ex.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toList());
                problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ApiConstantes.CONSTRAINT_VIOLATION);
                problemDetail.setProperty("errors", constraintErrors);
            }
            case DataIntegrityViolationException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                String message = ApiConstantes.MSG_DATA_INTEGRITY_VIOLATION;
                if (ex.getMessage().toLowerCase().contains("documento_identidad")) {
                    message = ApiConstantes.MSG_DATA_INTEGRITY_DOCUMENT;
                } else if (ex.getMessage().toLowerCase().contains("email")) {
                    message = ApiConstantes.MSG_DATA_INTEGRITY_EMAIL;
                }
                logGateway.warn(action, ApiConstantes.LOG_DATA_INTEGRITY_VIOLATION + ex.getMessage(), ex);
                problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, message);
            }
            case UsuarioNotFoundException ex -> {
                httpStatus = HttpStatus.NOT_FOUND;
                logGateway.warn(action, "Usuario no encontrado: " + ex.getMessage(), ex);
                problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
            }
            case BusinessRuleException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                logGateway.warn(action, "Regla de negocio violada: " + ex.getMessage(), ex);
                problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
            }
            case AuthenticationException ex -> {
                httpStatus = HttpStatus.UNAUTHORIZED;
                logGateway.warn(action, "Credenciales inválidas: " + ex.getMessage(), ex);
                problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
            }
            case IllegalArgumentException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                logGateway.warn(action, ApiConstantes.LOG_CLIENT_ERROR + ex.getMessage(), ex);
                problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
            }
            default -> {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                logGateway.error(action, ApiConstantes.LOG_SERVER_ERROR + error.getMessage(), error);
                problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ApiConstantes.MSG_UNEXPECTED_ERROR);
            }
        }

        problemDetail.setInstance(request.uri());

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(BodyInserters.fromValue(problemDetail));
    }
}

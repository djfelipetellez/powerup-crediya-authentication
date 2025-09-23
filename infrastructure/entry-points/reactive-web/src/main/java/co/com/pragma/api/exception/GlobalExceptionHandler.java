package co.com.pragma.api.exception;

import co.com.pragma.model.common.exceptions.BusinessRuleException;
import co.com.pragma.model.common.gateways.LogGateway;
import co.com.pragma.model.usuario.exceptions.UsuarioNotFoundException;
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

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static co.com.pragma.api.util.ApiConstantes.*;

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
        String action = LOG_GLOBAL_EXCEPTION_HANDLER;
        final HttpStatus httpStatus;
        final ProblemDetail problemDetail;

        switch (error) {
            case ConstraintViolationException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                logGateway.warn(action, CONSTRAINT_VIOLATION + ex.getMessage(), ex);

                problemDetail = ProblemDetail.forStatus(httpStatus);
                problemDetail.setType(URI.create(PROBLEM_TYPE_VALIDATION_FAILED));
                problemDetail.setTitle(TITLE_VALIDATION_FAILED);
                problemDetail.setDetail(DETAIL_VALIDATION_FAILED);

                // Información valiosa adicional
                List<String> validationErrors = ex.getConstraintViolations().stream()
                        .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                        .collect(Collectors.toList());
                problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_VALIDATION_FAILED);
                problemDetail.setProperty(PROPERTY_VALIDATION_ERRORS, validationErrors);
            }
            case DataIntegrityViolationException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                logGateway.warn(action, LOG_DATA_INTEGRITY_VIOLATION + ex.getMessage(), ex);

                problemDetail = ProblemDetail.forStatus(httpStatus);
                problemDetail.setType(URI.create(PROBLEM_TYPE_DATA_CONFLICT));
                problemDetail.setTitle(TITLE_DATA_CONFLICT);
                problemDetail.setDetail(DETAIL_DATA_CONFLICT);
                problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_DUPLICATE_DOCUMENT);

                // Información específica según el campo en conflicto
                if (ex.getMessage().toLowerCase().contains("documento_identidad")) {
                    problemDetail.setProperty(PROPERTY_CONFLICT_FIELD, "documento_identidad");
                    problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_DUPLICATE_DOCUMENT);
                    problemDetail.setProperty(PROPERTY_SUGGESTION, SUGGESTION_USE_DIFFERENT_DOCUMENT);
                } else if (ex.getMessage().toLowerCase().contains("email")) {
                    problemDetail.setProperty(PROPERTY_CONFLICT_FIELD, "email");
                    problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_DUPLICATE_EMAIL);
                    problemDetail.setProperty(PROPERTY_SUGGESTION, SUGGESTION_USE_DIFFERENT_EMAIL);
                }
                problemDetail.setProperty(PROPERTY_INSTANCE_ID, request.exchange().getRequest().getId());
            }
            case UsuarioNotFoundException ex -> {
                httpStatus = HttpStatus.NOT_FOUND;
                logGateway.warn(action, "Usuario no encontrado: " + ex.getMessage(), ex);

                problemDetail = ProblemDetail.forStatus(httpStatus);
                problemDetail.setType(URI.create(PROBLEM_TYPE_USER_NOT_FOUND));
                problemDetail.setTitle(TITLE_USER_NOT_FOUND);
                problemDetail.setDetail(DETAIL_USER_NOT_FOUND);
                problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_USER_NOT_FOUND);
                problemDetail.setProperty(PROPERTY_SUGGESTION, SUGGESTION_CHECK_EMAIL_ID);

                // Información sobre criterios de búsqueda si están disponibles
                if (ex.getMessage().contains("@")) {
                    problemDetail.setProperty(PROPERTY_SEARCH_CRITERIA, "email");
                } else {
                    problemDetail.setProperty(PROPERTY_SEARCH_CRITERIA, "id");
                }
            }
            case BusinessRuleException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                logGateway.warn(action, "Regla de negocio violada: " + ex.getMessage(), ex);

                problemDetail = ProblemDetail.forStatus(httpStatus);
                problemDetail.setType(URI.create(PROBLEM_TYPE_BUSINESS_RULE));
                problemDetail.setTitle(TITLE_BUSINESS_RULE);
                problemDetail.setDetail(ex.getMessage()); // El mensaje específico de la regla
                problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_BUSINESS_RULE);
            }
            case org.springframework.security.authentication.BadCredentialsException ex -> {
                httpStatus = HttpStatus.UNAUTHORIZED;
                logGateway.warn(action, "Credenciales inválidas: " + ex.getMessage(), ex);

                problemDetail = ProblemDetail.forStatus(httpStatus);
                if (ex.getMessage() != null && ex.getMessage().contains("Token expirado")) {
                    problemDetail.setType(URI.create(PROBLEM_TYPE_BASE + "token-expired"));
                    problemDetail.setTitle("Token Expirado");
                    problemDetail.setDetail("El token de autenticación ha expirado");
                    problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_TOKEN_EXPIRED);
                } else {
                    problemDetail.setType(URI.create(PROBLEM_TYPE_BASE + "authentication-failed"));
                    problemDetail.setTitle("Credenciales Inválidas");
                    problemDetail.setDetail("Las credenciales proporcionadas son incorrectas");
                    problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_AUTHENTICATION_FAILED);
                    problemDetail.setProperty(PROPERTY_SUGGESTION, SUGGESTION_CHECK_CREDENTIALS);
                }
            }
            case IllegalArgumentException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                logGateway.warn(action, LOG_CLIENT_ERROR + ex.getMessage(), ex);

                problemDetail = ProblemDetail.forStatus(httpStatus);
                problemDetail.setType(URI.create(PROBLEM_TYPE_INVALID_REQUEST));
                problemDetail.setTitle(TITLE_INVALID_REQUEST);
                problemDetail.setDetail(ex.getMessage());
                problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_INVALID_REQUEST);
            }
            default -> {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                logGateway.error(action, LOG_SERVER_ERROR + error.getMessage(), error);

                problemDetail = ProblemDetail.forStatus(httpStatus);
                problemDetail.setType(URI.create(PROBLEM_TYPE_SERVER_ERROR));
                problemDetail.setTitle(TITLE_SERVER_ERROR);
                problemDetail.setDetail(DETAIL_SERVER_ERROR);
                problemDetail.setProperty(PROPERTY_ERROR_CODE, ERROR_CODE_SERVER_ERROR);
                problemDetail.setProperty(PROPERTY_INSTANCE_ID, request.exchange().getRequest().getId());
            }
        }

        // Establecer la instancia para rastreo
        problemDetail.setInstance(request.uri());

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(BodyInserters.fromValue(problemDetail));
    }
}
package co.com.pragma.api.exception;

import co.com.pragma.api.util.ApiConstantes;
import co.com.pragma.api.util.LoggingUtil;
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
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext,
                                  ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties().getResources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {
        Throwable error = getError(request);
        String action = ApiConstantes.LOG_GLOBAL_EXCEPTION_HANDLER;
        final Map<String, Object> errorResponse = new HashMap<>();
        final HttpStatus httpStatus;

        switch (error) {
            case ConstraintViolationException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                LoggingUtil.warn(action, ApiConstantes.CONSTRAINT_VIOLATION + ex.getMessage(), ex);
                List<String> constraintErrors = ex.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.toList());
                errorResponse.put(ApiConstantes.KEY_MESSAGE, ApiConstantes.CONSTRAINT_VIOLATION);
                errorResponse.put(ApiConstantes.KEY_ERRORS, constraintErrors);
            }
            case DataIntegrityViolationException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                String message = ApiConstantes.MSG_DATA_INTEGRITY_VIOLATION;
                if (ex.getMessage().toLowerCase().contains("documento_identidad")) {
                    message = ApiConstantes.MSG_DATA_INTEGRITY_DOCUMENT;
                } else if (ex.getMessage().toLowerCase().contains("email")) {
                    message = ApiConstantes.MSG_DATA_INTEGRITY_EMAIL;
                }
                LoggingUtil.warn(action, ApiConstantes.LOG_DATA_INTEGRITY_VIOLATION + ex.getMessage(), ex);
                errorResponse.put(ApiConstantes.KEY_MESSAGE, message);
            }
            case IllegalArgumentException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                LoggingUtil.warn(action, ApiConstantes.LOG_CLIENT_ERROR + ex.getMessage(), ex);
                errorResponse.put(ApiConstantes.KEY_MESSAGE, ex.getMessage());
            }
            default -> {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                LoggingUtil.error(action, ApiConstantes.LOG_SERVER_ERROR + error.getMessage(), error);
                errorResponse.put(ApiConstantes.KEY_MESSAGE, ApiConstantes.MSG_UNEXPECTED_ERROR);
            }
        }

        errorResponse.put(ApiConstantes.KEY_STATUS, httpStatus.value());
        errorResponse.put(ApiConstantes.KEY_ERROR, httpStatus.getReasonPhrase());
        errorResponse.put(ApiConstantes.KEY_PATH, request.path());

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }
}

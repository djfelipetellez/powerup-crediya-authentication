package co.com.pragma.api.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

class RequestValidatorTest {

    @Mock
    private Validator validator;

    private RequestValidator requestValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestValidator = new RequestValidator(validator);
    }

    @Test
    void validate_withNoViolations_shouldReturnBody() {
        String body = "valid body";
        when(validator.validate(body)).thenReturn(Collections.emptySet());

        Mono<String> result = requestValidator.validate(body);

        StepVerifier.create(result)
                .expectNext(body)
                .verifyComplete();
    }

    @Test
    void validate_withViolations_shouldReturnError() {
        String body = "invalid body";
        Set<ConstraintViolation<String>> violations = new HashSet<>();
        violations.add(null); // In a real scenario, you would create a mock ConstraintViolation
        when(validator.validate(body)).thenReturn(violations);

        Mono<String> result = requestValidator.validate(body);

        StepVerifier.create(result)
                .expectError(ConstraintViolationException.class)
                .verify();
    }
}

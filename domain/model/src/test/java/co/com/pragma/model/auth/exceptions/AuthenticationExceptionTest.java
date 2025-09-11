package co.com.pragma.model.auth.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationExceptionTest {

    @Test
    void constructor_withMessage_shouldSetMessage() {
        String message = "Credenciales inválidas";
        
        AuthenticationException exception = new AuthenticationException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_withMessageAndCause_shouldSetMessageAndCause() {
        String message = "Error de autenticación";
        Throwable cause = new RuntimeException("Causa raíz");
        
        AuthenticationException exception = new AuthenticationException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void constructor_withNullMessage_shouldAcceptNull() {
        AuthenticationException exception = new AuthenticationException(null);
        
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_withNullCause_shouldAcceptNull() {
        String message = "Test message";
        
        AuthenticationException exception = new AuthenticationException(message, null);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void exception_shouldBeRuntimeException() {
        AuthenticationException exception = new AuthenticationException("test");
        
        assertInstanceOf(RuntimeException.class, exception);
    }
}
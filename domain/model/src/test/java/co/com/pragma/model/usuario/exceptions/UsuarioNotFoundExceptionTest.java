package co.com.pragma.model.usuario.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Given
        String expectedMessage = "Usuario no encontrado con documento: 12345 y email: test@test.com";

        // When
        UsuarioNotFoundException exception = new UsuarioNotFoundException(expectedMessage);

        // Then
        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(true);
    }

    @Test
    void shouldCreateExceptionWithNullMessage() {
        // When
        UsuarioNotFoundException exception = new UsuarioNotFoundException(null);

        // Then
        assertNull(exception.getMessage());
        assertTrue(true);
    }

    @Test
    void shouldCreateExceptionWithEmptyMessage() {
        // Given
        String emptyMessage = "";

        // When
        UsuarioNotFoundException exception = new UsuarioNotFoundException(emptyMessage);

        // Then
        assertEquals(emptyMessage, exception.getMessage());
        assertTrue(true);
    }
}
package co.com.pragma.model.common.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessRuleExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Given
        String expectedMessage = "Ya existe un usuario registrado con ese email";

        // When
        BusinessRuleException exception = new BusinessRuleException(expectedMessage);

        // Then
        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(true);
    }

    @Test
    void shouldCreateExceptionWithNullMessage() {
        // When
        BusinessRuleException exception = new BusinessRuleException(null);

        // Then
        assertNull(exception.getMessage());
        assertTrue(true);
    }

    @Test
    void shouldCreateExceptionWithEmptyMessage() {
        // Given
        String emptyMessage = "";

        // When
        BusinessRuleException exception = new BusinessRuleException(emptyMessage);

        // Then
        assertEquals(emptyMessage, exception.getMessage());
        assertTrue(true);
    }

    @Test
    void shouldCreateExceptionWithBusinessRuleMessage() {
        // Given
        String businessRuleMessage = "El salario base debe estar entre 0 y 15000000";

        // When
        BusinessRuleException exception = new BusinessRuleException(businessRuleMessage);

        // Then
        assertEquals(businessRuleMessage, exception.getMessage());
        assertTrue(true);
    }
}
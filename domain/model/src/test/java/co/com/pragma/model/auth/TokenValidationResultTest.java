package co.com.pragma.model.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenValidationResultTest {

    @Test
    void valid_shouldCreateValidTokenValidationResult() {
        Integer userId = 1;
        String email = "test@example.com";
        String role = "ADMIN";
        String documentoIdentidad = "12345678";
        Long exp = 1234567890L;

        TokenValidationResult result = TokenValidationResult.valid(userId, email, role, documentoIdentidad, exp);

        assertTrue(result.valid());
        assertEquals(userId, result.userId());
        assertEquals(email, result.email());
        assertEquals(role, result.role());
        assertEquals(documentoIdentidad, result.documentoIdentidad());
        assertEquals(exp, result.exp());
        assertNull(result.error());
    }

    @Test
    void invalid_shouldCreateInvalidTokenValidationResult() {
        String errorMessage = "Token expired";

        TokenValidationResult result = TokenValidationResult.invalid(errorMessage);

        assertFalse(result.valid());
        assertNull(result.userId());
        assertNull(result.email());
        assertNull(result.role());
        assertNull(result.documentoIdentidad());
        assertNull(result.exp());
        assertEquals(errorMessage, result.error());
    }

    @Test
    void constructor_shouldCreateTokenValidationResultWithAllFields() {
        boolean valid = true;
        Integer userId = 2;
        String email = "user@test.com";
        String role = "USER";
        String documentoIdentidad = "87654321";
        Long exp = 9876543210L;
        String error = null;

        TokenValidationResult result = new TokenValidationResult(valid, userId, email, role, documentoIdentidad, exp, error);

        assertEquals(valid, result.valid());
        assertEquals(userId, result.userId());
        assertEquals(email, result.email());
        assertEquals(role, result.role());
        assertEquals(documentoIdentidad, result.documentoIdentidad());
        assertEquals(exp, result.exp());
        assertEquals(error, result.error());
    }

    @Test
    void constructor_shouldCreateInvalidTokenValidationResultWithError() {
        boolean valid = false;
        String errorMessage = "Invalid token format";

        TokenValidationResult result = new TokenValidationResult(valid, null, null, null, null, null, errorMessage);

        assertFalse(result.valid());
        assertNull(result.userId());
        assertNull(result.email());
        assertNull(result.role());
        assertNull(result.documentoIdentidad());
        assertNull(result.exp());
        assertEquals(errorMessage, result.error());
    }
}
package co.com.pragma.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteValidationRequestTest {

    @Test
    void constructor_withValidData_shouldCreateInstance() {
        String documentoIdentidad = "12345678";
        String email = "test@example.com";
        
        ClienteValidationRequest request = new ClienteValidationRequest(documentoIdentidad, email);
        
        assertEquals(documentoIdentidad, request.documentoIdentidad());
        assertEquals(email, request.email());
    }

    @Test
    void constructor_withNullValues_shouldAcceptNull() {
        ClienteValidationRequest request = new ClienteValidationRequest(null, null);
        
        assertNull(request.documentoIdentidad());
        assertNull(request.email());
    }

    @Test
    void constructor_withEmptyValues_shouldAcceptEmpty() {
        String emptyDocumento = "";
        String emptyEmail = "";
        
        ClienteValidationRequest request = new ClienteValidationRequest(emptyDocumento, emptyEmail);
        
        assertEquals(emptyDocumento, request.documentoIdentidad());
        assertEquals(emptyEmail, request.email());
    }

    @Test
    void equals_withSameData_shouldBeEqual() {
        String documento = "12345678";
        String email = "test@example.com";
        
        ClienteValidationRequest request1 = new ClienteValidationRequest(documento, email);
        ClienteValidationRequest request2 = new ClienteValidationRequest(documento, email);
        
        assertEquals(request1, request2);
    }

    @Test
    void equals_withDifferentDocumento_shouldNotBeEqual() {
        ClienteValidationRequest request1 = new ClienteValidationRequest("12345678", "test@example.com");
        ClienteValidationRequest request2 = new ClienteValidationRequest("87654321", "test@example.com");
        
        assertNotEquals(request1, request2);
    }

    @Test
    void equals_withDifferentEmail_shouldNotBeEqual() {
        ClienteValidationRequest request1 = new ClienteValidationRequest("12345678", "test1@example.com");
        ClienteValidationRequest request2 = new ClienteValidationRequest("12345678", "test2@example.com");
        
        assertNotEquals(request1, request2);
    }

    @Test
    void hashCode_withSameData_shouldBeSame() {
        String documento = "12345678";
        String email = "test@example.com";
        
        ClienteValidationRequest request1 = new ClienteValidationRequest(documento, email);
        ClienteValidationRequest request2 = new ClienteValidationRequest(documento, email);
        
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void toString_shouldContainBothFields() {
        String documento = "12345678";
        String email = "test@example.com";
        
        ClienteValidationRequest request = new ClienteValidationRequest(documento, email);
        String result = request.toString();
        
        assertTrue(result.contains(documento));
        assertTrue(result.contains(email));
        assertTrue(result.contains("ClienteValidationRequest"));
    }

    @Test
    void documentoIdentidad_shouldReturnCorrectValue() {
        String documento = "CC-12345678";
        ClienteValidationRequest request = new ClienteValidationRequest(documento, "email@test.com");
        
        assertEquals(documento, request.documentoIdentidad());
    }

    @Test
    void email_shouldReturnCorrectValue() {
        String email = "cliente@banco.com";
        ClienteValidationRequest request = new ClienteValidationRequest("12345678", email);
        
        assertEquals(email, request.email());
    }

    @Test
    void constructor_withSpecialCharacters_shouldAccept() {
        String documentoEspecial = "CC-1.234.567-8";
        String emailEspecial = "cliente+test@banco-ejemplo.com";
        
        ClienteValidationRequest request = new ClienteValidationRequest(documentoEspecial, emailEspecial);
        
        assertEquals(documentoEspecial, request.documentoIdentidad());
        assertEquals(emailEspecial, request.email());
    }

    @Test
    void constructor_withLongValues_shouldAccept() {
        String documentoLargo = "a".repeat(100);
        String emailLargo = "very-long-email-address@very-long-domain-name.com";
        
        ClienteValidationRequest request = new ClienteValidationRequest(documentoLargo, emailLargo);
        
        assertEquals(documentoLargo, request.documentoIdentidad());
        assertEquals(emailLargo, request.email());
    }

    @Test
    void equals_withNullValues_shouldWork() {
        ClienteValidationRequest request1 = new ClienteValidationRequest(null, null);
        ClienteValidationRequest request2 = new ClienteValidationRequest(null, null);
        ClienteValidationRequest request3 = new ClienteValidationRequest("12345678", null);
        
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
    }

    @Test
    void hashCode_withNullValues_shouldNotThrow() {
        ClienteValidationRequest request = new ClienteValidationRequest(null, null);
        
        assertDoesNotThrow(request::hashCode);
    }

    @Test
    void toString_withNullValues_shouldNotThrow() {
        ClienteValidationRequest request = new ClienteValidationRequest(null, null);
        
        assertDoesNotThrow(() -> {
            String result = request.toString();
            assertTrue(result.contains("ClienteValidationRequest"));
        });
    }
}
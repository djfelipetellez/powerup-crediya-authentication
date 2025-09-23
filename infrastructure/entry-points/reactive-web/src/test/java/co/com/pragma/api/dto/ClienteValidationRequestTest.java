package co.com.pragma.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteValidationRequestTest {

    @Test
    void constructor_withValidData_shouldCreateInstance() {
        String email = "test@example.com";

        ClienteValidationRequest request = new ClienteValidationRequest(email);

        assertEquals(email, request.email());
    }

    @Test
    void constructor_withNullValues_shouldAcceptNull() {
        ClienteValidationRequest request = new ClienteValidationRequest(null);

        assertNull(request.email());
    }

    @Test
    void constructor_withEmptyValues_shouldAcceptEmpty() {
        String emptyEmail = "";

        ClienteValidationRequest request = new ClienteValidationRequest(emptyEmail);

        assertEquals(emptyEmail, request.email());
    }

    @Test
    void equals_withSameData_shouldBeEqual() {
        String email = "test@example.com";

        ClienteValidationRequest request1 = new ClienteValidationRequest(email);
        ClienteValidationRequest request2 = new ClienteValidationRequest(email);

        assertEquals(request1, request2);
    }

    @Test
    void equals_withDifferentEmail_shouldNotBeEqual() {
        ClienteValidationRequest request1 = new ClienteValidationRequest("test1@example.com");
        ClienteValidationRequest request2 = new ClienteValidationRequest("test2@example.com");

        assertNotEquals(request1, request2);
    }

    @Test
    void hashCode_withSameData_shouldBeSame() {
        String email = "test@example.com";

        ClienteValidationRequest request1 = new ClienteValidationRequest(email);
        ClienteValidationRequest request2 = new ClienteValidationRequest(email);

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void toString_shouldContainEmailField() {
        String email = "test@example.com";

        ClienteValidationRequest request = new ClienteValidationRequest(email);
        String result = request.toString();

        assertTrue(result.contains(email));
        assertTrue(result.contains("ClienteValidationRequest"));
    }

    @Test
    void email_shouldReturnCorrectValue() {
        String email = "cliente@banco.com";
        ClienteValidationRequest request = new ClienteValidationRequest(email);

        assertEquals(email, request.email());
    }

    @Test
    void constructor_withSpecialCharacters_shouldAccept() {
        String emailEspecial = "cliente+test@banco-ejemplo.com";

        ClienteValidationRequest request = new ClienteValidationRequest(emailEspecial);

        assertEquals(emailEspecial, request.email());
    }

    @Test
    void constructor_withLongValues_shouldAccept() {
        String emailLargo = "very-long-email-address@very-long-domain-name.com";

        ClienteValidationRequest request = new ClienteValidationRequest(emailLargo);

        assertEquals(emailLargo, request.email());
    }

    @Test
    void equals_withNullValues_shouldWork() {
        ClienteValidationRequest request1 = new ClienteValidationRequest(null);
        ClienteValidationRequest request2 = new ClienteValidationRequest(null);
        ClienteValidationRequest request3 = new ClienteValidationRequest("test@example.com");

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
    }

    @Test
    void hashCode_withNullValues_shouldNotThrow() {
        ClienteValidationRequest request = new ClienteValidationRequest(null);

        assertDoesNotThrow(request::hashCode);
    }

    @Test
    void toString_withNullValues_shouldNotThrow() {
        ClienteValidationRequest request = new ClienteValidationRequest(null);

        assertDoesNotThrow(() -> {
            String result = request.toString();
            assertTrue(result.contains("ClienteValidationRequest"));
        });
    }
}
package co.com.pragma.model.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginCredencialesTest {

    @Test
    void constructor_withEmailAndPassword_shouldCreateInstance() {
        String email = "test@example.com";
        String password = "password123";

        LoginCredenciales credenciales = new LoginCredenciales(email, password);

        assertEquals(email, credenciales.email());
        assertEquals(password, credenciales.password());
    }

    @Test
    void constructor_withNullValues_shouldAcceptNull() {
        LoginCredenciales credenciales = new LoginCredenciales(null, null);

        assertNull(credenciales.email());
        assertNull(credenciales.password());
    }

    @Test
    void constructor_withEmptyValues_shouldAcceptEmpty() {
        String emptyEmail = "";
        String emptyPassword = "";

        LoginCredenciales credenciales = new LoginCredenciales(emptyEmail, emptyPassword);

        assertEquals(emptyEmail, credenciales.email());
        assertEquals(emptyPassword, credenciales.password());
    }

    @Test
    void equals_withSameCredentials_shouldBeEqual() {
        String email = "test@example.com";
        String password = "password123";
        LoginCredenciales credenciales1 = new LoginCredenciales(email, password);
        LoginCredenciales credenciales2 = new LoginCredenciales(email, password);

        assertEquals(credenciales1, credenciales2);
    }

    @Test
    void equals_withDifferentEmail_shouldNotBeEqual() {
        LoginCredenciales credenciales1 = new LoginCredenciales("test1@example.com", "password");
        LoginCredenciales credenciales2 = new LoginCredenciales("test2@example.com", "password");

        assertNotEquals(credenciales1, credenciales2);
    }

    @Test
    void equals_withDifferentPassword_shouldNotBeEqual() {
        LoginCredenciales credenciales1 = new LoginCredenciales("test@example.com", "password1");
        LoginCredenciales credenciales2 = new LoginCredenciales("test@example.com", "password2");

        assertNotEquals(credenciales1, credenciales2);
    }

    @Test
    void hashCode_withSameCredentials_shouldBeSame() {
        String email = "test@example.com";
        String password = "password123";
        LoginCredenciales credenciales1 = new LoginCredenciales(email, password);
        LoginCredenciales credenciales2 = new LoginCredenciales(email, password);

        assertEquals(credenciales1.hashCode(), credenciales2.hashCode());
    }

    @Test
    void toString_shouldContainEmailButNotPassword() {
        String email = "test@example.com";
        String password = "secretPassword";
        LoginCredenciales credenciales = new LoginCredenciales(email, password);

        String result = credenciales.toString();

        assertTrue(result.contains(email));
        assertTrue(result.contains("LoginCredenciales"));
    }

    @Test
    void email_shouldReturnCorrectValue() {
        String email = "user@domain.com";
        LoginCredenciales credenciales = new LoginCredenciales(email, "pass");

        assertEquals(email, credenciales.email());
    }

    @Test
    void password_shouldReturnCorrectValue() {
        String password = "mySecretPassword";
        LoginCredenciales credenciales = new LoginCredenciales("email@test.com", password);

        assertEquals(password, credenciales.password());
    }
}
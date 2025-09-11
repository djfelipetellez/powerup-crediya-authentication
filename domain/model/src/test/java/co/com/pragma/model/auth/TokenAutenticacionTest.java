package co.com.pragma.model.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenAutenticacionTest {

    @Test
    void constructor_withToken_shouldCreateInstance() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        
        TokenAutenticacion tokenAutenticacion = new TokenAutenticacion(token);
        
        assertEquals(token, tokenAutenticacion.token());
    }

    @Test
    void constructor_withNullToken_shouldAcceptNull() {
        TokenAutenticacion tokenAutenticacion = new TokenAutenticacion(null);
        
        assertNull(tokenAutenticacion.token());
    }

    @Test
    void constructor_withEmptyToken_shouldAcceptEmpty() {
        String emptyToken = "";
        
        TokenAutenticacion tokenAutenticacion = new TokenAutenticacion(emptyToken);
        
        assertEquals(emptyToken, tokenAutenticacion.token());
    }

    @Test
    void equals_withSameToken_shouldBeEqual() {
        String token = "test-token";
        TokenAutenticacion token1 = new TokenAutenticacion(token);
        TokenAutenticacion token2 = new TokenAutenticacion(token);
        
        assertEquals(token1, token2);
    }

    @Test
    void equals_withDifferentToken_shouldNotBeEqual() {
        TokenAutenticacion token1 = new TokenAutenticacion("token1");
        TokenAutenticacion token2 = new TokenAutenticacion("token2");
        
        assertNotEquals(token1, token2);
    }

    @Test
    void hashCode_withSameToken_shouldBeSame() {
        String token = "test-token";
        TokenAutenticacion token1 = new TokenAutenticacion(token);
        TokenAutenticacion token2 = new TokenAutenticacion(token);
        
        assertEquals(token1.hashCode(), token2.hashCode());
    }

    @Test
    void toString_shouldContainToken() {
        String token = "test-token";
        TokenAutenticacion tokenAutenticacion = new TokenAutenticacion(token);
        
        String result = tokenAutenticacion.toString();
        
        assertTrue(result.contains(token));
        assertTrue(result.contains("TokenAutenticacion"));
    }
}
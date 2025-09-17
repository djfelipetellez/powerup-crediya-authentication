package co.com.pragma.security.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;
    private final Long testExpiration = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        String testSecret = "myTestSecretKeyThatIsLongEnoughForHS256Algorithm";
        ReflectionTestUtils.setField(jwtProvider, "secret", testSecret);
        ReflectionTestUtils.setField(jwtProvider, "expiration", testExpiration);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String username = "test@example.com";
        Integer userId = 1;
        String roleName = "ADMIN";
        String documentoIdentidad = "12345678";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void generateToken_shouldIncludeCorrectClaims() {
        String username = "test@example.com";
        Integer userId = 1;
        String roleName = "ADMIN";
        String documentoIdentidad = "12345678";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        Claims claims = jwtProvider.getClaims(token);

        assertEquals(username, claims.getSubject());
        assertEquals(userId, claims.get("userId", Integer.class));
        assertEquals(documentoIdentidad, claims.get("documentoIdentidad", String.class));

        @SuppressWarnings("unchecked")
        List<Map<String, String>> roles = (List<Map<String, String>>) claims.get("roles");
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("ROLE_ADMIN", roles.getFirst().get("authority"));
    }

    @Test
    void generateToken_shouldSetCorrectExpiration() {
        String username = "test@example.com";
        Integer userId = 1;
        String roleName = "ADMIN";
        String documentoIdentidad = "12345678";

        Date beforeGeneration = new Date();
        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        Date afterGeneration = new Date();

        Claims claims = jwtProvider.getClaims(token);
        Date expiration = claims.getExpiration();

        assertTrue(expiration.after(beforeGeneration));
        assertTrue(expiration.before(new Date(afterGeneration.getTime() + testExpiration + 1000)));
    }

    @Test
    void getClaims_shouldReturnCorrectClaims() {
        String username = "test@example.com";
        Integer userId = 1;
        String roleName = "ADMIN";
        String documentoIdentidad = "12345678";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        Claims claims = jwtProvider.getClaims(token);

        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertEquals(userId, claims.get("userId", Integer.class));
        assertEquals(documentoIdentidad, claims.get("documentoIdentidad", String.class));
    }

    @Test
    void getClaims_shouldThrowException_forInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(JwtException.class, () -> jwtProvider.getClaims(invalidToken));
    }

    @Test
    void isTokenExpired_shouldReturnFalse_forValidToken() {
        String username = "test@example.com";
        Integer userId = 1;
        String roleName = "ADMIN";
        String documentoIdentidad = "12345678";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        Boolean isExpired = jwtProvider.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_shouldReturnTrue_forExpiredToken() {
        // Create a token with very short expiration (1 millisecond)
        ReflectionTestUtils.setField(jwtProvider, "expiration", 1L);

        String username = "test@example.com";
        Integer userId = 1;
        String roleName = "ADMIN";
        String documentoIdentidad = "12345678";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);

        // Wait to ensure token is expired
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Boolean isExpired = jwtProvider.isTokenExpired(token);

        assertTrue(isExpired);
    }

    @Test
    void getUsernameFromToken_shouldReturnCorrectUsername() {
        String username = "test@example.com";
        Integer userId = 1;
        String roleName = "ADMIN";
        String documentoIdentidad = "12345678";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        String extractedUsername = jwtProvider.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void getUserIdFromToken_shouldReturnCorrectUserId() {
        String username = "test@example.com";
        Integer userId = 123;
        String roleName = "ADMIN";
        String documentoIdentidad = "12345678";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        Integer extractedUserId = jwtProvider.getUserIdFromToken(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    void getDocumentoIdentidadFromToken_shouldReturnCorrectDocument() {
        String username = "test@example.com";
        Integer userId = 1;
        String roleName = "ADMIN";
        String documentoIdentidad = "98765432";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        String extractedDocument = jwtProvider.getDocumentoIdentidadFromToken(token);

        assertEquals(documentoIdentidad, extractedDocument);
    }

    @Test
    void generateToken_shouldHandleDifferentRoles() {
        String username = "cliente@example.com";
        Integer userId = 2;
        String roleName = "CLIENTE";
        String documentoIdentidad = "11111111";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        Claims claims = jwtProvider.getClaims(token);

        @SuppressWarnings("unchecked")
        List<Map<String, String>> roles = (List<Map<String, String>>) claims.get("roles");
        assertEquals("ROLE_CLIENTE", roles.getFirst().get("authority"));
    }

    @Test
    void generateToken_shouldHandleLowercaseRole() {
        String username = "asesor@example.com";
        Integer userId = 3;
        String roleName = "asesor";
        String documentoIdentidad = "22222222";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        Claims claims = jwtProvider.getClaims(token);

        @SuppressWarnings("unchecked")
        List<Map<String, String>> roles = (List<Map<String, String>>) claims.get("roles");
        assertEquals("ROLE_ASESOR", roles.getFirst().get("authority"));
    }

    @Test
    void generateToken_shouldHandleSpecialCharactersInUsername() {
        String username = "test+user@example-domain.com";
        Integer userId = 4;
        String roleName = "ADMIN";
        String documentoIdentidad = "33333333";

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        String extractedUsername = jwtProvider.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void generateToken_shouldHandleNullDocumentoIdentidad() {
        String username = "test@example.com";
        Integer userId = 1;
        String roleName = "ADMIN";
        String documentoIdentidad = null;

        String token = jwtProvider.generateToken(username, userId, roleName, documentoIdentidad);
        String extractedDocument = jwtProvider.getDocumentoIdentidadFromToken(token);

        assertNull(extractedDocument);
    }

    @Test
    void getClaims_shouldHandleTokenWithDifferentSecret() {
        // Generate token with one secret
        String token = jwtProvider.generateToken("test@example.com", 1, "ADMIN", "12345678");

        // Change secret and try to parse
        ReflectionTestUtils.setField(jwtProvider, "secret", "differentSecretKey");

        assertThrows(JwtException.class, () -> jwtProvider.getClaims(token));
    }


    @Test
    void getClaims_shouldValidateTokenSignature() {
        // Create a valid token
        String token = jwtProvider.generateToken("test@example.com", 1, "ADMIN", "12345678");

        // Manually create an invalid token by changing the signature
        String[] parts = token.split("\\.");
        String invalidToken = parts[0] + "." + parts[1] + ".invalidSignature";

        assertThrows(JwtException.class, () -> jwtProvider.getClaims(invalidToken));
    }
}
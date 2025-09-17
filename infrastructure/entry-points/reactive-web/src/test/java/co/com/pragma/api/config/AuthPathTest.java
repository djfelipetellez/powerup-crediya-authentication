package co.com.pragma.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.*;

class AuthPathTest {

    @Test
    void constructor_shouldCreateEmptyInstance() {
        AuthPath authPath = new AuthPath();
        
        assertNull(authPath.getLogin());
    }

    @Test
    void setLogin_shouldSetValue() {
        AuthPath authPath = new AuthPath();
        String loginPath = "/api/v1/login";
        
        authPath.setLogin(loginPath);
        
        assertEquals(loginPath, authPath.getLogin());
    }

    @Test
    void getLogin_shouldReturnSetValue() {
        AuthPath authPath = new AuthPath();
        String loginPath = "/auth/login";
        
        authPath.setLogin(loginPath);
        String result = authPath.getLogin();
        
        assertEquals(loginPath, result);
    }

    @Test
    void setLogin_withNull_shouldAcceptNull() {
        AuthPath authPath = new AuthPath();
        
        authPath.setLogin(null);
        
        assertNull(authPath.getLogin());
    }

    @Test
    void setLogin_withEmptyString_shouldAcceptEmpty() {
        AuthPath authPath = new AuthPath();
        String emptyPath = "";
        
        authPath.setLogin(emptyPath);
        
        assertEquals(emptyPath, authPath.getLogin());
    }

    @Test
    void setLogin_withDifferentPathFormats_shouldAccept() {
        AuthPath authPath = new AuthPath();
        String[] paths = {
            "/login",
            "/api/login",
            "/api/v1/auth/login",
            "/authentication/signin",
            "login" // without leading slash
        };
        
        for (String path : paths) {
            authPath.setLogin(path);
            assertEquals(path, authPath.getLogin());
        }
    }

    @Test
    void setLogin_withSpecialCharacters_shouldAccept() {
        AuthPath authPath = new AuthPath();
        String specialPath = "/api/v1/auth/login?redirect=true&locale=es";
        
        authPath.setLogin(specialPath);
        
        assertEquals(specialPath, authPath.getLogin());
    }

    @Test
    void setLogin_withLongPath_shouldAccept() {
        AuthPath authPath = new AuthPath();
        String longPath = "/api/v1/authentication/login/with/very/long/path/structure";
        
        authPath.setLogin(longPath);
        
        assertEquals(longPath, authPath.getLogin());
    }

    @Test
    void multipleSetLogin_shouldUpdateValue() {
        AuthPath authPath = new AuthPath();
        
        authPath.setLogin("/login1");
        assertEquals("/login1", authPath.getLogin());
        
        authPath.setLogin("/login2");
        assertEquals("/login2", authPath.getLogin());
        
        authPath.setLogin("/login3");
        assertEquals("/login3", authPath.getLogin());
    }

    @Test
    void hasComponentAnnotation() {
        assertTrue(AuthPath.class.isAnnotationPresent(Component.class));
    }

    @Test
    void hasConfigurationPropertiesAnnotation() {
        assertTrue(AuthPath.class.isAnnotationPresent(ConfigurationProperties.class));
        
        ConfigurationProperties annotation = AuthPath.class.getAnnotation(ConfigurationProperties.class);
        assertEquals("api.paths.auth", annotation.prefix());
    }

    @Test
    void setLogin_withWhitespace_shouldAccept() {
        AuthPath authPath = new AuthPath();
        String pathWithSpaces = "  /api/login  ";
        
        authPath.setLogin(pathWithSpaces);
        
        assertEquals(pathWithSpaces, authPath.getLogin());
    }

    @Test
    void setLogin_withTabsAndNewlines_shouldAccept() {
        AuthPath authPath = new AuthPath();
        String pathWithSpecialChars = "/api\tlogin\ntest";
        
        authPath.setLogin(pathWithSpecialChars);
        
        assertEquals(pathWithSpecialChars, authPath.getLogin());
    }

    @Test
    void toString_shouldNotThrow() {
        AuthPath authPath = new AuthPath();
        authPath.setLogin("/api/login");
        
        assertDoesNotThrow(() -> {
            String result = authPath.toString();
            assertNotNull(result);
        });
    }

    @Test
    void toString_withNullLogin_shouldNotThrow() {
        AuthPath authPath = new AuthPath();
        authPath.setLogin(null);
        
        assertDoesNotThrow(() -> {
            String result = authPath.toString();
            assertNotNull(result);
        });
    }

    @Test
    void equals_withSameLogin_shouldBeEqual() {
        AuthPath authPath1 = new AuthPath();
        AuthPath authPath2 = new AuthPath();
        String loginPath = "/api/login";
        
        authPath1.setLogin(loginPath);
        authPath2.setLogin(loginPath);
        
        assertEquals(authPath1.getLogin(), authPath2.getLogin());
    }

    @Test
    void setLogin_chainedCalls_shouldWork() {
        AuthPath authPath = new AuthPath();
        
        authPath.setLogin("/first");
        String first = authPath.getLogin();
        
        authPath.setLogin("/second");
        String second = authPath.getLogin();
        
        assertEquals("/first", first);
        assertEquals("/second", second);
    }
}
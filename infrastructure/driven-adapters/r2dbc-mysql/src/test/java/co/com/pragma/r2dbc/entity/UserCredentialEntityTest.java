package co.com.pragma.r2dbc.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserCredentialEntityTest {

    @Test
    void noArgsConstructor_shouldCreateEmptyInstance() {
        UserCredentialEntity entity = new UserCredentialEntity();

        assertNull(entity.getId());
        assertNull(entity.getEmail());
        assertNull(entity.getPassword());
        assertNull(entity.getIdUsuario());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getLastLoginAt());
        assertFalse(entity.isActive());
    }

    @Test
    void allArgsConstructor_shouldCreateInstanceWithAllFields() {
        Integer id = 1;
        String email = "test@example.com";
        String password = "hashedPassword123";
        Integer idUsuario = 100;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime lastLoginAt = LocalDateTime.now().minusHours(1);
        boolean active = true;

        UserCredentialEntity entity = new UserCredentialEntity(
                id, email, password, idUsuario, createdAt, lastLoginAt, active
        );

        assertEquals(id, entity.getId());
        assertEquals(email, entity.getEmail());
        assertEquals(password, entity.getPassword());
        assertEquals(idUsuario, entity.getIdUsuario());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(lastLoginAt, entity.getLastLoginAt());
        assertTrue(entity.isActive());
    }

    @Test
    void builder_shouldCreateInstanceWithAllFields() {
        Integer id = 2;
        String email = "user@domain.com";
        String password = "securePassword";
        Integer idUsuario = 200;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime lastLoginAt = LocalDateTime.now().minusDays(1);
        boolean active = false;

        UserCredentialEntity entity = UserCredentialEntity.builder()
                .id(id)
                .email(email)
                .password(password)
                .idUsuario(idUsuario)
                .createdAt(createdAt)
                .lastLoginAt(lastLoginAt)
                .active(active)
                .build();

        assertEquals(id, entity.getId());
        assertEquals(email, entity.getEmail());
        assertEquals(password, entity.getPassword());
        assertEquals(idUsuario, entity.getIdUsuario());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(lastLoginAt, entity.getLastLoginAt());
        assertFalse(entity.isActive());
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        String email = "partial@test.com";
        Integer idUsuario = 300;

        UserCredentialEntity entity = UserCredentialEntity.builder()
                .email(email)
                .idUsuario(idUsuario)
                .active(true)
                .build();

        assertNull(entity.getId());
        assertEquals(email, entity.getEmail());
        assertNull(entity.getPassword());
        assertEquals(idUsuario, entity.getIdUsuario());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getLastLoginAt());
        assertTrue(entity.isActive());
    }

    @Test
    void toBuilder_shouldCreateBuilderFromExistingInstance() {
        UserCredentialEntity original = UserCredentialEntity.builder()
                .id(1)
                .email("original@test.com")
                .password("originalPassword")
                .idUsuario(100)
                .active(true)
                .build();

        UserCredentialEntity modified = original.toBuilder()
                .email("modified@test.com")
                .password("newPassword")
                .active(false)
                .build();

        assertEquals(original.getId(), modified.getId());
        assertEquals("modified@test.com", modified.getEmail());
        assertEquals("newPassword", modified.getPassword());
        assertEquals(original.getIdUsuario(), modified.getIdUsuario());
        assertEquals(original.getCreatedAt(), modified.getCreatedAt());
        assertEquals(original.getLastLoginAt(), modified.getLastLoginAt());
        assertFalse(modified.isActive());
    }

    @Test
    void setters_shouldUpdateAllFields() {
        UserCredentialEntity entity = new UserCredentialEntity();
        Integer id = 5;
        String email = "setter@test.com";
        String password = "setterPassword";
        Integer idUsuario = 500;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime lastLoginAt = LocalDateTime.now().minusMinutes(30);
        boolean active = true;

        entity.setId(id);
        entity.setEmail(email);
        entity.setPassword(password);
        entity.setIdUsuario(idUsuario);
        entity.setCreatedAt(createdAt);
        entity.setLastLoginAt(lastLoginAt);
        entity.setActive(active);

        assertEquals(id, entity.getId());
        assertEquals(email, entity.getEmail());
        assertEquals(password, entity.getPassword());
        assertEquals(idUsuario, entity.getIdUsuario());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(lastLoginAt, entity.getLastLoginAt());
        assertTrue(entity.isActive());
    }

    @Test
    void setters_withNullValues_shouldAcceptNull() {
        UserCredentialEntity entity = UserCredentialEntity.builder()
                .id(1)
                .email("test@example.com")
                .password("password")
                .idUsuario(100)
                .active(true)
                .build();

        entity.setId(null);
        entity.setEmail(null);
        entity.setPassword(null);
        entity.setIdUsuario(null);
        entity.setCreatedAt(null);
        entity.setLastLoginAt(null);
        entity.setActive(false);

        assertNull(entity.getId());
        assertNull(entity.getEmail());
        assertNull(entity.getPassword());
        assertNull(entity.getIdUsuario());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getLastLoginAt());
        assertFalse(entity.isActive());
    }

    @Test
    void active_defaultValue_shouldBeFalse() {
        UserCredentialEntity entity = new UserCredentialEntity();

        assertFalse(entity.isActive());
    }

    @Test
    void active_toggleValue_shouldWork() {
        UserCredentialEntity entity = new UserCredentialEntity();

        entity.setActive(true);
        assertTrue(entity.isActive());

        entity.setActive(false);
        assertFalse(entity.isActive());
    }

    @Test
    void email_withSpecialCharacters_shouldBeAccepted() {
        String specialEmail = "user+test@sub-domain.example.com";

        UserCredentialEntity entity = UserCredentialEntity.builder()
                .email(specialEmail)
                .build();

        assertEquals(specialEmail, entity.getEmail());
    }

    @Test
    void password_withComplexValue_shouldBeAccepted() {
        String complexPassword = "P@ssw0rd!2023#$%^&*()_+-=[]{}|;':\",./<>?";

        UserCredentialEntity entity = UserCredentialEntity.builder()
                .password(complexPassword)
                .build();

        assertEquals(complexPassword, entity.getPassword());
    }

    @Test
    void dateTime_withPreciseValues_shouldMaintainPrecision() {
        LocalDateTime preciseTime = LocalDateTime.of(2023, 12, 25, 14, 30, 45, 123456789);

        UserCredentialEntity entity = UserCredentialEntity.builder()
                .createdAt(preciseTime)
                .lastLoginAt(preciseTime.minusHours(2))
                .build();

        assertEquals(preciseTime, entity.getCreatedAt());
        assertEquals(preciseTime.minusHours(2), entity.getLastLoginAt());
    }

    @Test
    void idUsuario_withLargeValue_shouldBeAccepted() {
        Integer largeId = Integer.MAX_VALUE;

        UserCredentialEntity entity = UserCredentialEntity.builder()
                .idUsuario(largeId)
                .build();

        assertEquals(largeId, entity.getIdUsuario());
    }

    @Test
    void builder_chainedCalls_shouldWork() {
        LocalDateTime now = LocalDateTime.now();

        UserCredentialEntity entity = UserCredentialEntity.builder()
                .id(1)
                .email("chain@test.com")
                .password("chainPassword")
                .idUsuario(123)
                .createdAt(now)
                .lastLoginAt(now.minusHours(1))
                .active(true)
                .build();

        assertNotNull(entity);
        assertEquals(1, entity.getId());
        assertEquals("chain@test.com", entity.getEmail());
        assertEquals("chainPassword", entity.getPassword());
        assertEquals(123, entity.getIdUsuario());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now.minusHours(1), entity.getLastLoginAt());
        assertTrue(entity.isActive());
    }
}
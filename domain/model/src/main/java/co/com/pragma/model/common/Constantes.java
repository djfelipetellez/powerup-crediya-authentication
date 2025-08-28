package co.com.pragma.model.common;

public final class Constantes {

    private Constantes() {
        throw new IllegalStateException("Utility class");
    }

    // === VALIDATION MESSAGES ===
    public static final String MSG_USUARIO_NULL = "El usuario no puede ser nulo";
    public static final String MSG_ROLE_ID_NULL = "El ID del rol no puede ser nulo";
    public static final String MSG_SALARY_NULL = "El salario base no puede ser nulo";
    public static final String MSG_EMAIL_EMPTY = "El email del usuario no puede estar vacío";
    public static final String MSG_EMAIL_INVALID = "El email no puede estar vacío";
    public static final String MSG_ROLE_NOT_EXISTS = "El rol especificado no existe en el sistema";
    public static final String MSG_EMAIL_DUPLICATE = "Ya existe un usuario registrado con ese email";
    public static final String MSG_SALARY_RANGE = "El salario base debe estar entre ";

    // === BUSINESS RULES ===
    public static final String SALARY_MIN = "0";
    public static final String SALARY_MAX = "15000000";
}
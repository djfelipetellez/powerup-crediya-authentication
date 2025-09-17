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
    public static final String MSG_ROLE_NOT_EXISTS = "El rol especificado no existe en el sistema";
    public static final String MSG_EMAIL_DUPLICATE = "Ya existe un usuario registrado con ese email";
    public static final String MSG_SALARY_RANGE = "El salario base debe estar entre ";

    // === BUSINESS RULES ===
    public static final String SALARY_MIN = "0";
    public static final String SALARY_MAX = "15000000";

    // === SECURITY MESSAGES ===
    public static final String MSG_TOKEN_NOT_FOUND = "Token de autorización requerido";
    public static final String MSG_INVALID_TOKEN_FORMAT = "Formato de autorización inválido";
    public static final String MSG_ACCESS_PUBLIC_ROUTE = "Acceso permitido a ruta pública";
    public static final String MSG_TOKEN_EXTRACTED = "Token extraído exitosamente";
    public static final String MSG_INVALID_CREDENTIALS = "Credenciales inválidas";
    public static final String MSG_USER_INACTIVE = "Usuario inactivo";

    // === AUTHENTICATION LOG MESSAGES ===
    public static final String LOG_AUTH_START = "Iniciando autenticación para email: ";
    public static final String LOG_AUTH_SUCCESS = "Autenticación exitosa para email: ";
    public static final String LOG_AUTH_ERROR = "Error en autenticación: ";
}
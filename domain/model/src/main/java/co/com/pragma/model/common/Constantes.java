package co.com.pragma.model.common;

public final class Constantes {

    private Constantes() {
        throw new IllegalStateException("Utility class");
    }

    // === VALIDATION MESSAGES ===
    public static final String MSG_USUARIO_NULL = "El usuario no puede ser nulo";
    public static final String MSG_SALARY_NULL = "El salario base no puede ser nulo";
    public static final String MSG_ROLE_NOT_EXISTS = "El rol especificado no existe en el sistema";
    public static final String MSG_EMAIL_DUPLICATE = "Ya existe un usuario registrado con ese email";
    public static final String MSG_SALARY_RANGE = "El salario base debe estar entre ";

    // === BUSINESS RULES ===
    public static final String SALARY_MIN = "0";
    public static final String SALARY_MAX = "15000000";

    // === SECURITY MESSAGES ===
    public static final String MSG_TOKEN_EXTRACTED = "Token extraído exitosamente";
    public static final String MSG_INVALID_CREDENTIALS = "Credenciales inválidas";
    public static final String MSG_USER_INACTIVE = "Usuario inactivo";

    // === AUTHENTICATION LOG MESSAGES ===
    public static final String LOG_AUTH_START = "Iniciando autenticación para email: ";
    public static final String LOG_AUTH_SUCCESS = "Autenticación exitosa para email: ";
    public static final String LOG_AUTH_ERROR = "Error en autenticación: ";
    public static final String LOG_USER_FOUND = "Usuario encontrado en base de datos para email: ";
    public static final String LOG_USER_NOT_FOUND = "Usuario no encontrado en base de datos para email: ";
    public static final String LOG_USER_STATUS_CHECK = "Verificando estado del usuario para email: ";
    public static final String LOG_USER_INACTIVE_ATTEMPT = "Intento de login con usuario inactivo para email: ";
    public static final String LOG_PASSWORD_VALIDATION = "Validando contraseña para email: ";
    public static final String LOG_PASSWORD_INVALID = "Contraseña inválida para email: ";
    public static final String LOG_TOKEN_GENERATION_START = "Iniciando generación de token para usuario ID: ";
    public static final String LOG_TOKEN_GENERATION_SUCCESS = "Token generado exitosamente para usuario ID: ";
    public static final String LOG_AUTH_DURATION = "Proceso de autenticación completado en: ";
    public static final String LOG_USER_DATA_FETCH = "Obteniendo datos completos del usuario para ID: ";
}
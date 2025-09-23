package co.com.pragma.security.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SecurityConstants {

    // PROBLEM DETAIL CONSTANTES
    public static final String PROBLEM_TYPE_BASE = "https://api.pragma.com/problems/";
    public static final String PROBLEM_TYPE_TOKEN_EXPIRED = PROBLEM_TYPE_BASE + "token-expired";
    public static final String PROBLEM_TYPE_AUTHENTICATION = PROBLEM_TYPE_BASE + "authentication-failed";
    public static final String PROBLEM_TYPE_ACCESS_DENIED = PROBLEM_TYPE_BASE + "access-denied";

    // PROBLEM DETAIL TITLES
    public static final String TITLE_TOKEN_EXPIRED = "Token Expirado";
    public static final String TITLE_AUTHENTICATION = "Credenciales Inválidas";
    public static final String TITLE_ACCESS_DENIED = "Acceso Denegado";

    // PROBLEM DETAIL DESCRIPTIONS
    public static final String DETAIL_TOKEN_EXPIRED = "El token de autenticación ha expirado";
    public static final String DETAIL_AUTHENTICATION = "Las credenciales proporcionadas son incorrectas";
    public static final String DETAIL_ACCESS_DENIED = "No tienes permisos suficientes para realizar esta acción";

    // ERROR CODES
    public static final String ERROR_CODE_ACCESS_DENIED = "ACCESS_DENIED";

    // PROBLEM DETAIL PROPERTIES
    public static final String PROPERTY_ERROR_CODE = "error_code";
    public static final String PROPERTY_SUGGESTION = "suggestion";

    // SUGGESTIONS
    public static final String SUGGESTION_CONTACT_ADMIN = "Contacte al administrador del sistema";

    // LOGGING CONSTANTES
    public static final String LOG_AUTHENTICATION_ENTRY_POINT = "authentication-entry-point";
    public static final String LOG_ACCESS_DENIED_HANDLER = "access-denied-handler";

    // MESSAGES
    public static final String MSG_AUTH_ERROR_FOR_PATH = "Error de autenticación para ruta: %s - %s";
    public static final String MSG_ACCESS_DENIED_FOR_PATH = "Acceso denegado para ruta: %s - %s";
}
package co.com.pragma.api.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ApiConstantes {

    // OPEN API CONSTANTES
    // --- Operation IDs ---
    public static final String OP_REGISTRAR_USUARIO = "registrarUsuario";
    public static final String OP_REGISTRAR_ROL = "registrarRol";

    // --- Descriptions ---
    public static final String DESC_REGISTRAR_USUARIO = "Crea un nuevo usuario en el sistema";
    public static final String DESC_REGISTRAR_ROL = "Crea un nuevo rol en el sistema";
    public static final String DESC_VALIDAR_USUARIO_EXISTE = "Valida si existe un usuario por documento de identidad";
    public static final String DESC_LOGIN = "Autentica a un usuario y devuelve un token JWT";

    // --- Tags ---
    public static final String TAG_USUARIOS = "Usuarios";
    public static final String TAG_ROLES = "Roles";
    public static final String TAG_AUTENTICACION = "Autenticación";

    // --- Mensajes open api genericos ---
    public static final String MSG_400 = "Petición inválida";
    public static final String MSG_401 = "Credenciales inválidas";
    public static final String MSG_500 = "Error interno del servidor";

    // --- Mensajes específicos ---
    public static final String MSG_USUARIO_CREADO = "Usuario creado exitosamente";
    public static final String MSG_ROL_CREADO = "Rol creado exitosamente";
    public static final String MSG_LOGIN_EXITOSO = "Login exitoso";
    public static final String MSG_ESTADO_EXISTENCIA_USUARIO = "Estado de existencia del usuario";

    // LOGGING CONSTANTES
    public static final String LOG_GLOBAL_EXCEPTION_HANDLER = "GlobalExceptionHandling";
    public static final String CONSTRAINT_VIOLATION = "Constraint violation";
    public static final String LOG_DATA_INTEGRITY_VIOLATION = "Data integrity violation";
    public static final String LOG_CLIENT_ERROR = "Client error: ";
    public static final String LOG_SERVER_ERROR = "Server error: ";


    // MEDIA TYPE CONSTANTES
    public static final String MEDIA_TYPE_JSON = "application/json";
    public static final String MEDIA_TYPE_PROBLEM_JSON = "application/problem+json";

    // HANDLER LOG CONSTANTES
    public static final String LOG_HANDLER = "Handler";
    public static final String LOG_REGISTRAR_USUARIO = "registrarUsuario";
    public static final String LOG_REGISTRAR_ROL = "registrarRol";
    public static final String LOG_LOGIN = "login";

    // HANDLER MESSAGE CONSTANTES
    public static final String MSG_INICIANDO_CONSULTAR_USUARIO = "=== INICIANDO consultarUsuario endpoint ===";
    public static final String MSG_FINALIZANDO_CONSULTAR_USUARIO = "=== FINALIZANDO consultarUsuario endpoint - Consulta exitosa ===";
    public static final String MSG_INICIANDO_VALIDATE_TOKEN = "=== INICIANDO validateToken endpoint ===";
    public static final String MSG_FINALIZANDO_VALIDATE_TOKEN = "=== FINALIZANDO validateToken endpoint - Respuesta: %s ===";
    public static final String MSG_DATOS_ENTRANTES_EMAIL = "DATOS ENTRANTES - Email para consulta: %s";
    public static final String MSG_PATH_PARAMETER_EXTRAIDO = "Path parameter extraído correctamente: %s";
    public static final String MSG_ENVIANDO_CONSULTA_USE_CASE = "Enviando consulta al use case para email: %s";
    public static final String MSG_USUARIO_ENCONTRADO = "Usuario encontrado: %s";
    public static final String MSG_DATOS_SALIENTES_RESPUESTA = "DATOS SALIENTES - Respuesta: %s";
    public static final String MSG_USUARIO_NO_ENCONTRADO_CONSULTA = "Usuario no encontrado durante consulta: %s";
    public static final String MSG_ERROR_CONSULTANDO_USUARIO = "Error consultando datos de usuario: %s";
    public static final String MSG_TOKEN_RECIBIDO_VALIDACION = "Token recibido para validación: %s...";
    public static final String MSG_REQUEST_VALIDADO = "Request validado correctamente";
    public static final String MSG_ENVIANDO_TOKEN_USE_CASE = "Enviando token al use case para validación";
    public static final String MSG_RESULTADO_USE_CASE = "Resultado del use case - válido: %s, error: %s";
    public static final String MSG_ERROR_VALIDATE_TOKEN = "Error en validateToken: %s";
    public static final String MSG_INTENTO_REGISTRO_USUARIO = "Intento de registro de usuario: email=%s, doc=%s";
    public static final String MSG_USUARIO_REGISTRADO = "Usuario registrado id=%d, email=%s";
    public static final String MSG_INTENTO_REGISTRO_ROL = "Intento de registro de rol: nombre=%s";
    public static final String MSG_ROL_REGISTRADO = "Rol registrado id=%d, nombre=%s";
    public static final String MSG_INTENTO_LOGIN = "Intento de login para email: %s";
    public static final String MSG_LOGIN_EXITOSO_EMAIL = "Login exitoso para email: %s";

    // RESPONSE MESSAGE CONSTANTES
    public static final String RESPONSE_LOGIN_EXITOSO = "Login exitoso";
    public static final String RESPONSE_TOKEN_KEY = "token";
    public static final String RESPONSE_MESSAGE_KEY = "message";

    // PROBLEM DETAIL CONSTANTES
    public static final String PROBLEM_TYPE_BASE = "https://api.pragma.com/problems/";
    public static final String PROBLEM_TYPE_VALIDATION_FAILED = PROBLEM_TYPE_BASE + "validation-failed";
    public static final String PROBLEM_TYPE_DATA_CONFLICT = PROBLEM_TYPE_BASE + "data-conflict";
    public static final String PROBLEM_TYPE_USER_NOT_FOUND = PROBLEM_TYPE_BASE + "user-not-found";
    public static final String PROBLEM_TYPE_BUSINESS_RULE = PROBLEM_TYPE_BASE + "business-rule-violation";
    public static final String PROBLEM_TYPE_INVALID_REQUEST = PROBLEM_TYPE_BASE + "invalid-request";
    public static final String PROBLEM_TYPE_SERVER_ERROR = PROBLEM_TYPE_BASE + "server-error";

    // PROBLEM DETAIL TITLES
    public static final String TITLE_VALIDATION_FAILED = "Validation Failed";
    public static final String TITLE_DATA_CONFLICT = "Data Conflict";
    public static final String TITLE_USER_NOT_FOUND = "Usuario No Encontrado";
    public static final String TITLE_BUSINESS_RULE = "Regla de Negocio Violada";
    public static final String TITLE_INVALID_REQUEST = "Petición Inválida";
    public static final String TITLE_SERVER_ERROR = "Error Interno del Servidor";

    // PROBLEM DETAIL DESCRIPTIONS
    public static final String DETAIL_VALIDATION_FAILED = "Los datos enviados no cumplen las validaciones requeridas";
    public static final String DETAIL_DATA_CONFLICT = "El recurso ya existe con los datos proporcionados";
    public static final String DETAIL_USER_NOT_FOUND = "No se encontró un usuario con los criterios especificados";
    public static final String DETAIL_BUSINESS_RULE = "La operación solicitada viola una regla de negocio";
    public static final String DETAIL_INVALID_REQUEST = "La petición contiene datos inválidos";
    public static final String DETAIL_SERVER_ERROR = "Ocurrió un error inesperado en el servidor";

    // ERROR CODES
    public static final String ERROR_CODE_VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String ERROR_CODE_DUPLICATE_DOCUMENT = "DUPLICATE_DOCUMENT";
    public static final String ERROR_CODE_DUPLICATE_EMAIL = "DUPLICATE_EMAIL";
    public static final String ERROR_CODE_USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String ERROR_CODE_BUSINESS_RULE = "BUSINESS_RULE_VIOLATION";
    public static final String ERROR_CODE_INVALID_REQUEST = "INVALID_REQUEST";
    public static final String ERROR_CODE_SERVER_ERROR = "SERVER_ERROR";
    public static final String ERROR_CODE_TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String ERROR_CODE_AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";

    // PROBLEM DETAIL PROPERTIES
    public static final String PROPERTY_ERROR_CODE = "error_code";
    public static final String PROPERTY_VALIDATION_ERRORS = "validation_errors";
    public static final String PROPERTY_CONFLICT_FIELD = "conflict_field";
    public static final String PROPERTY_SUGGESTION = "suggestion";
    public static final String PROPERTY_SEARCH_CRITERIA = "search_criteria";
    public static final String PROPERTY_INSTANCE_ID = "instance_id";

    // SUGGESTIONS
    public static final String SUGGESTION_CHECK_EMAIL_ID = "Verifique que el email o ID sean correctos";
    public static final String SUGGESTION_USE_DIFFERENT_EMAIL = "Utilice un email diferente";
    public static final String SUGGESTION_USE_DIFFERENT_DOCUMENT = "Utilice un documento de identidad diferente";
    public static final String SUGGESTION_CHECK_CREDENTIALS = "Verifique sus credenciales de acceso";
}
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

    // --- Tags ---
    public static final String TAG_USUARIOS = "Usuarios";
    public static final String TAG_ROLES = "Roles";

    // --- Mensajes open api genericos ---
    public static final String MSG_200 = "Operación exitosa";
    public static final String MSG_201 = "Recurso creado exitosamente";
    public static final String MSG_400 = "Petición inválida";
    public static final String MSG_500 = "Error interno del servidor";

    // LOGGING CONSTANTES
    public static final String LOG_GLOBAL_EXCEPTION_HANDLER = "GlobalExceptionHandling";
    public static final String VALIDATION_ERROR = "Error de Validacion: ";
    public static final String CONSTRAINT_VIOLATION = "Constraint violation";
    public static final String LOG_DATA_INTEGRITY_VIOLATION = "Data integrity violation";
    public static final String LOG_CLIENT_ERROR = "Client error: ";
    public static final String LOG_SERVER_ERROR = "Server error: ";


    // EXCEPTION CONSTANTES
    public static final String MSG_DATA_INTEGRITY_VIOLATION = "A record with the provided information already exists.";
    public static final String MSG_DATA_INTEGRITY_DOCUMENT = "Ya existe un usuario registrado con ese documento de identidad.";
    public static final String MSG_DATA_INTEGRITY_EMAIL = "Ya existe un usuario registrado con ese email.";
    public static final String MSG_UNEXPECTED_ERROR = "An unexpected error occurred";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_ERRORS = "errors";
    public static final String KEY_STATUS = "status";
    public static final String KEY_ERROR = "error";
    public static final String KEY_PATH = "path";
}
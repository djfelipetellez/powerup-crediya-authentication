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
    public static final String DESC_VALIDAR_DATOS_USUARIO = "Valida los datos de un usuario existente";

    // --- Tags ---
    public static final String TAG_USUARIOS = "Usuarios";
    public static final String TAG_ROLES = "Roles";
    public static final String TAG_AUTENTICACION = "Autenticación";

    // --- Mensajes open api genericos ---
    public static final String MSG_200 = "Operación exitosa";
    public static final String MSG_201 = "Recurso creado exitosamente";
    public static final String MSG_400 = "Petición inválida";
    public static final String MSG_401 = "Credenciales inválidas";
    public static final String MSG_403 = "No tienes permisos para acceder a este recurso";
    public static final String MSG_500 = "Error interno del servidor";

    // --- Mensajes específicos ---
    public static final String MSG_USUARIO_CREADO = "Usuario creado exitosamente";
    public static final String MSG_ROL_CREADO = "Rol creado exitosamente";
    public static final String MSG_LOGIN_EXITOSO = "Login exitoso";
    public static final String MSG_ESTADO_EXISTENCIA_USUARIO = "Estado de existencia del usuario";
    public static final String MSG_DATOS_VALIDADOS = "Datos validados correctamente";

    // LOGGING CONSTANTES
    public static final String LOG_GLOBAL_EXCEPTION_HANDLER = "GlobalExceptionHandling";
    public static final String VALIDATION_ERROR = "Error de Validacion: ";
    public static final String CONSTRAINT_VIOLATION = "Constraint violation";
    public static final String LOG_DATA_INTEGRITY_VIOLATION = "Data integrity violation";
    public static final String LOG_CLIENT_ERROR = "Client error: ";
    public static final String LOG_SERVER_ERROR = "Server error: ";


    // MEDIA TYPE CONSTANTES
    public static final String MEDIA_TYPE_JSON = "application/json";
    public static final String MEDIA_TYPE_PROBLEM_JSON = "application/problem+json";

    // EXCEPTION CONSTANTES
    public static final String MSG_DATA_INTEGRITY_VIOLATION = "A record with the provided information already exists.";
    public static final String MSG_DATA_INTEGRITY_DOCUMENT = "Ya existe un usuario registrado con ese documento de identidad.";
    public static final String MSG_DATA_INTEGRITY_EMAIL = "Ya existe un usuario registrado con ese email.";
    public static final String MSG_UNEXPECTED_ERROR = "An unexpected error occurred";
}
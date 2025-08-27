package co.com.pragma.api.util;

public final class ApiConstantes {

    public static final String TITLE_APP = "API AUTENTICACION";
    public static final String VERSION_APP = "1.0";

    // --- Paths ---
    public static final String PATH_USUARIOS = "/api/v1/usuarios";
    public static final String PATH_ROLES = "/api/v1/roles";

    // --- Operation IDs ---
    public static final String OP_REGISTRAR_USUARIO = "registrarUsuario";
    public static final String OP_REGISTRAR_ROL = "registrarRol";

    // --- Summaries ---
    public static final String SUMMARY_REGISTRAR_USUARIO = "Registrar usuario";
    public static final String SUMMARY_REGISTRAR_ROL = "Registrar rol";

    // --- Descriptions ---
    public static final String DESC_REGISTRAR_USUARIO = "Crea un nuevo usuario en el sistema";
    public static final String DESC_REGISTRAR_ROL = "Crea un nuevo rol en el sistema";

    // --- Tags ---
    public static final String TAG_USUARIOS = "Usuarios";
    public static final String TAG_ROLES = "Roles";

    // --- Mensajes genéricos ---
    public static final String MSG_200 = "Operación exitosa";
    public static final String MSG_400 = "Petición inválida";
    public static final String MSG_500 = "Error interno del servidor";
}

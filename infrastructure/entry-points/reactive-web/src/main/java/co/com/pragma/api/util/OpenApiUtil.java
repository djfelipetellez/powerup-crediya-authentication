package co.com.pragma.api.util;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenApiUtil {

    @Bean
    public OpenAPI customOpenAPI() {
        // --- Components con schemas ---
        Components components = new Components()
                .addSchemas("UsuarioRegistroRequestDto", new ObjectSchema()
                        .addProperty("nombre", new Schema<>().type("string").example("Juan"))
                        .addProperty("apellido", new Schema<>().type("string").example("Pérez"))
                        .addProperty("email", new Schema<>().type("string").example("juan@correo.com"))
                        .addProperty("documentoIdentidad", new Schema<>().type("string").example("12345678"))
                        .addProperty("telefono", new Schema<>().type("string").example("3001234567"))
                        .addProperty("salarioBase", new Schema<>().type("number").format("double").example(1500.0))
                        .addProperty("idRol", new Schema<>().type("integer").example(1))
                )
                .addSchemas("UsuarioResponseDto", new ObjectSchema()
                        .addProperty("idUsuario", new Schema<>().type("integer").example(1))
                        .addProperty("nombre", new Schema<>().type("string").example("Juan"))
                        .addProperty("apellido", new Schema<>().type("string").example("Pérez"))
                        .addProperty("email", new Schema<>().type("string").example("juan@correo.com"))
                        .addProperty("documentoIdentidad", new Schema<>().type("string").example("12345678"))
                        .addProperty("telefono", new Schema<>().type("string").example("3001234567"))
                        .addProperty("salarioBase", new Schema<>().type("number").format("double").example(1500.0))
                        .addProperty("rol", new ObjectSchema()
                                .addProperty("idRol", new Schema<>().type("integer").example(1))
                                .addProperty("nombre", new Schema<>().type("string").example("ADMIN"))
                                .addProperty("descripcion", new Schema<>().type("string").example("Administrador"))
                        )
                )
                .addSchemas("RolRegistroRequestDto", new ObjectSchema()
                        .addProperty("nombre", new Schema<>().type("string").example("ADMIN"))
                        .addProperty("descripcion", new Schema<>().type("string").example("Administrador"))
                )
                .addSchemas("RoleResponseDto", new ObjectSchema()
                        .addProperty("idRol", new Schema<>().type("integer").example(1))
                        .addProperty("nombre", new Schema<>().type("string").example("ADMIN"))
                        .addProperty("descripcion", new Schema<>().type("string").example("Administrador"))
                );

        // --- Paths ---
        Paths paths = new Paths();

        // Endpoint Usuario
        Operation opUsuario = new Operation()
                .operationId(ApiConstantes.OP_REGISTRAR_USUARIO)
                .summary(ApiConstantes.SUMMARY_REGISTRAR_USUARIO)
                .description(ApiConstantes.DESC_REGISTRAR_USUARIO)
                .addTagsItem(ApiConstantes.TAG_USUARIOS)
                .requestBody(new RequestBody()
                        .description("Datos para registrar usuario")
                        .required(true)
                        .content(new Content().addMediaType("application/json",
                                new io.swagger.v3.oas.models.media.MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/UsuarioRegistroRequestDto"))
                        ))
                )
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse()
                                .description(ApiConstantes.MSG_200)
                                .content(new Content().addMediaType("application/json",
                                        new io.swagger.v3.oas.models.media.MediaType()
                                                .schema(new Schema<>().$ref("#/components/schemas/UsuarioResponseDto"))
                                ))
                        )
                        .addApiResponse("400", new ApiResponse().description(ApiConstantes.MSG_400))
                        .addApiResponse("500", new ApiResponse().description(ApiConstantes.MSG_500))
                );

        paths.addPathItem(ApiConstantes.PATH_USUARIOS, new PathItem().post(opUsuario));

        // Endpoint Rol
        Operation opRol = new Operation()
                .operationId(ApiConstantes.OP_REGISTRAR_ROL)
                .summary(ApiConstantes.SUMMARY_REGISTRAR_ROL)
                .description(ApiConstantes.DESC_REGISTRAR_ROL)
                .addTagsItem(ApiConstantes.TAG_ROLES)
                .requestBody(new RequestBody()
                        .description("Datos para registrar rol")
                        .required(true)
                        .content(new Content().addMediaType("application/json",
                                new io.swagger.v3.oas.models.media.MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/RolRegistroRequestDto"))
                        ))
                )
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse()
                                .description(ApiConstantes.MSG_200)
                                .content(new Content().addMediaType("application/json",
                                        new io.swagger.v3.oas.models.media.MediaType()
                                                .schema(new Schema<>().$ref("#/components/schemas/RoleResponseDto"))
                                ))
                        )
                        .addApiResponse("400", new ApiResponse().description(ApiConstantes.MSG_400))
                        .addApiResponse("500", new ApiResponse().description(ApiConstantes.MSG_500))
                );

        paths.addPathItem(ApiConstantes.PATH_ROLES, new PathItem().post(opRol));

        // --- OpenAPI ---
        return new OpenAPI()
                .info(new Info()
                        .title(ApiConstantes.TITLE_APP)
                        .version(ApiConstantes.VERSION_APP)
                        .description("Documentación del " + ApiConstantes.TITLE_APP))
                .components(components)
                .paths(paths);
    }
}

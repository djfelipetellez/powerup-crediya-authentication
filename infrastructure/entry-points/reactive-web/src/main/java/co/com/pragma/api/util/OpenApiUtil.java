package co.com.pragma.api.util;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
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
                // DTOs
                .addSchemas("UsuarioRegistroRequestDto", new ObjectSchema()
                        .addProperty(ApiConstantes.NOMBRE_PROPERTY, new Schema<>().type("string").example("Juan"))
                        .addProperty("apellido", new Schema<>().type("string").example("Pérez"))
                        .addProperty("email", new Schema<>().type("string").example("juan@correo.com"))
                        .addProperty("documentoIdentidad", new Schema<>().type("string").example("12345678"))
                        .addProperty("telefono", new Schema<>().type("string").example("3001234567"))
                        .addProperty("salarioBase", new Schema<>().type("number").format("double").example(1500.0))
                        .addProperty(ApiConstantes.ID_ROLE_PROPERTY, new Schema<>().type("integer").example(1))
                )
                .addSchemas("UsuarioResponseDto", new ObjectSchema()
                        .addProperty("idUsuario", new Schema<>().type("integer").example(1))
                        .addProperty(ApiConstantes.NOMBRE_PROPERTY, new Schema<>().type("string").example("Juan"))
                        .addProperty("apellido", new Schema<>().type("string").example("Pérez"))
                        .addProperty("email", new Schema<>().type("string").example("juan@correo.com"))
                        .addProperty("documentoIdentidad", new Schema<>().type("string").example("12345678"))
                        .addProperty("telefono", new Schema<>().type("string").example("3001234567"))
                        .addProperty("salarioBase", new Schema<>().type("number").format("double").example(1500.0))
                        .addProperty("rol", new ObjectSchema()
                                .addProperty(ApiConstantes.ID_ROLE_PROPERTY, new Schema<>().type("integer").example(1))
                                .addProperty(ApiConstantes.NOMBRE_PROPERTY, new Schema<>().type("string").example(ApiConstantes.USUARIO_PROPERTY))
                                .addProperty(ApiConstantes.DESCRIPTION_PROPERTY, new Schema<>().type("string").example(ApiConstantes.DESCRIPTION_PROPERTY))
                        )
                )
                .addSchemas("RolRegistroRequestDto", new ObjectSchema()
                        .addProperty(ApiConstantes.NOMBRE_PROPERTY, new Schema<>().type("string").example(ApiConstantes.USUARIO_PROPERTY))
                        .addProperty(ApiConstantes.DESCRIPTION_PROPERTY, new Schema<>().type("string").example(ApiConstantes.DESCRIPTION_PROPERTY))
                )
                .addSchemas("RoleResponseDto", new ObjectSchema()
                        .addProperty(ApiConstantes.ID_ROLE_PROPERTY, new Schema<>().type("integer").example(1))
                        .addProperty(ApiConstantes.NOMBRE_PROPERTY, new Schema<>().type("string").example(ApiConstantes.USUARIO_PROPERTY))
                        .addProperty(ApiConstantes.DESCRIPTION_PROPERTY, new Schema<>().type("string").example(ApiConstantes.DESCRIPTION_PROPERTY))
                )
                // Error Responses
                .addSchemas("UsuarioGenericErrorResponse", new ObjectSchema()
                        .addProperty(ApiConstantes.KEY_STATUS, new Schema<>().type("integer").example(500))
                        .addProperty(ApiConstantes.KEY_ERROR, new Schema<>().type("string").example("Internal Server Error"))
                        .addProperty(ApiConstantes.KEY_PATH, new Schema<>().type("string").example(ApiConstantes.PATH_USUARIOS))
                        .addProperty(ApiConstantes.KEY_MESSAGE, new Schema<>().type("string").example(ApiConstantes.MSG_UNEXPECTED_ERROR))
                )
                .addSchemas("RolGenericErrorResponse", new ObjectSchema()
                        .addProperty(ApiConstantes.KEY_STATUS, new Schema<>().type("integer").example(500))
                        .addProperty(ApiConstantes.KEY_ERROR, new Schema<>().type("string").example("Internal Server Error"))
                        .addProperty(ApiConstantes.KEY_PATH, new Schema<>().type("string").example(ApiConstantes.PATH_ROLES))
                        .addProperty(ApiConstantes.KEY_MESSAGE, new Schema<>().type("string").example(ApiConstantes.MSG_UNEXPECTED_ERROR))
                )
                .addSchemas("ValidationErrorResponse", new ObjectSchema()
                        .addProperty(ApiConstantes.KEY_STATUS, new Schema<>().type("integer").example(400))
                        .addProperty(ApiConstantes.KEY_ERROR, new Schema<>().type("string").example("Bad Request"))
                        .addProperty(ApiConstantes.KEY_PATH, new Schema<>().type("string").example(ApiConstantes.PATH_USUARIOS))
                        .addProperty(ApiConstantes.KEY_MESSAGE, new Schema<>().type("string").example(ApiConstantes.VALIDATION_ERROR))
                        .addProperty(ApiConstantes.KEY_ERRORS, new ArraySchema().items(new Schema<>().type("string")).example("email: El correo es obligatorio"))
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
                        .addApiResponse("201", new ApiResponse()
                                .description(ApiConstantes.MSG_200) // Assuming 201 is created
                                .content(new Content().addMediaType("application/json",
                                        new io.swagger.v3.oas.models.media.MediaType()
                                                .schema(new Schema<>().$ref("#/components/schemas/UsuarioResponseDto"))
                                ))
                        )
                        .addApiResponse("400", new ApiResponse()
                                .description(ApiConstantes.MSG_400)
                                .content(new Content().addMediaType("application/json",
                                        new io.swagger.v3.oas.models.media.MediaType()
                                                .schema(new Schema<>().$ref("#/components/schemas/ValidationErrorResponse"))
                                ))
                        )
                        .addApiResponse("500", new ApiResponse()
                                .description(ApiConstantes.MSG_500)
                                .content(new Content().addMediaType("application/json",
                                        new io.swagger.v3.oas.models.media.MediaType()
                                                .schema(new Schema<>().$ref("#/components/schemas/UsuarioGenericErrorResponse"))
                                ))
                        )
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
                        .addApiResponse("201", new ApiResponse()
                                .description(ApiConstantes.MSG_200) // Assuming 201 is created
                                .content(new Content().addMediaType("application/json",
                                        new io.swagger.v3.oas.models.media.MediaType()
                                                .schema(new Schema<>().$ref("#/components/schemas/RoleResponseDto"))
                                ))
                        )
                        .addApiResponse("400", new ApiResponse()
                                .description(ApiConstantes.MSG_400)
                        )
                        .addApiResponse("500", new ApiResponse()
                                .description(ApiConstantes.MSG_500)
                                .content(new Content().addMediaType("application/json",
                                        new io.swagger.v3.oas.models.media.MediaType()
                                                .schema(new Schema<>().$ref("#/components/schemas/RolGenericErrorResponse"))
                                ))
                        )
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
package co.com.pragma.api.util;

import co.com.pragma.api.dto.*;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.http.MediaType;
import org.springframework.web.ErrorResponse;

import static co.com.pragma.api.util.ApiConstantes.*;
import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;

@UtilityClass
public class OpenApiUtil {

    public Builder registrarUsuario(Builder builder) {
        return builder
                .operationId(OP_REGISTRAR_USUARIO)
                .description(DESC_REGISTRAR_USUARIO)
                .tag(TAG_USUARIOS)
                .requestBody(requestBodyBuilder()
                        .required(true)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(UsuarioRegistroRequestDto.class))))
                .response(responseBuilder().responseCode("201").description("Usuario creado exitosamente")
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(UsuarioResponseDto.class))))
                .response(responseBuilder().responseCode("400").description(MSG_400)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ErrorResponse.class))))
                .response(responseBuilder().responseCode("500").description(MSG_500)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ErrorResponse.class))));
    }

    public Builder registrarRol(Builder builder) {
        return builder
                .operationId(OP_REGISTRAR_ROL)
                .description(DESC_REGISTRAR_ROL)
                .tag(TAG_ROLES)
                .requestBody(requestBodyBuilder()
                        .required(true)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(RolRegistroRequestDto.class))))
                .response(responseBuilder().responseCode("201").description("Rol creado exitosamente")
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(RoleResponseDto.class))))
                .response(responseBuilder().responseCode("400").description(MSG_400)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ErrorResponse.class))))
                .response(responseBuilder().responseCode("500").description(MSG_500)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ErrorResponse.class))));
    }

    public Builder validarUsuarioExiste(Builder builder) {
        return builder
                .operationId("validarUsuarioExiste")
                .description("Valida si existe un usuario por documento de identidad")
                .tag(TAG_USUARIOS)
                .response(responseBuilder().responseCode("200").description("Estado de existencia del usuario")
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(Boolean.class))))
                .response(responseBuilder().responseCode("500").description(MSG_500)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ErrorResponse.class))));
    }

    public Builder validarDatosUsuario(Builder builder) {
        return builder
                .operationId("validarDatosUsuario")
                .description("Valida los datos de un usuario existente")
                .tag(TAG_USUARIOS)
                .requestBody(requestBodyBuilder()
                        .required(true)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ClienteValidationRequest.class))))
                .response(responseBuilder().responseCode("200").description("Datos validados correctamente"))
                .response(responseBuilder().responseCode("400").description(MSG_400)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ErrorResponse.class))))
                .response(responseBuilder().responseCode("500").description(MSG_500)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ErrorResponse.class))));
    }
}
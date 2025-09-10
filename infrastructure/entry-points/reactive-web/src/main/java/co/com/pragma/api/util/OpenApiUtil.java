package co.com.pragma.api.util;

import co.com.pragma.api.dto.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.http.ProblemDetail;

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
                        .content(contentBuilder().mediaType(MEDIA_TYPE_JSON)
                                .schema(schemaBuilder().implementation(UsuarioRegistroRequestDto.class))))
                .response(responseBuilder().responseCode("201").description(MSG_USUARIO_CREADO)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_JSON)
                                .schema(schemaBuilder().implementation(UsuarioResponseDto.class))))
                .response(responseBuilder().responseCode("400").description(MSG_400)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_PROBLEM_JSON)
                                .schema(schemaBuilder().implementation(ProblemDetail.class))))
                .response(responseBuilder().responseCode("500").description(MSG_500)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_PROBLEM_JSON)
                                .schema(schemaBuilder().implementation(ProblemDetail.class))));
    }

    public Builder registrarRol(Builder builder) {
        return builder
                .operationId(OP_REGISTRAR_ROL)
                .description(DESC_REGISTRAR_ROL)
                .tag(TAG_ROLES)
                .requestBody(requestBodyBuilder()
                        .required(true)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_JSON)
                                .schema(schemaBuilder().implementation(RolRegistroRequestDto.class))))
                .response(responseBuilder().responseCode("201").description(MSG_ROL_CREADO)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_JSON)
                                .schema(schemaBuilder().implementation(RoleResponseDto.class))))
                .response(responseBuilder().responseCode("400").description(MSG_400)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_PROBLEM_JSON)
                                .schema(schemaBuilder().implementation(ProblemDetail.class))))
                .response(responseBuilder().responseCode("500").description(MSG_500)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_PROBLEM_JSON)
                                .schema(schemaBuilder().implementation(ProblemDetail.class))));
    }

    public Builder validarUsuarioExiste(Builder builder) {
        return builder
                .operationId("validarUsuarioExiste")
                .description(DESC_VALIDAR_USUARIO_EXISTE)
                .tag(TAG_USUARIOS)
                .response(responseBuilder().responseCode("200").description(MSG_ESTADO_EXISTENCIA_USUARIO)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_JSON)
                                .schema(schemaBuilder().implementation(Boolean.class))))
                .response(responseBuilder().responseCode("500").description(MSG_500)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_PROBLEM_JSON)
                                .schema(schemaBuilder().implementation(ProblemDetail.class))));
    }

    public Builder login(Builder builder) {
        return builder
                .operationId("login")
                .description(DESC_LOGIN)
                .tag(TAG_AUTENTICACION)
                .requestBody(requestBodyBuilder()
                        .required(true)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_JSON)
                                .schema(schemaBuilder().implementation(LoginRequestDto.class))))
                .response(responseBuilder().responseCode("200").description(MSG_LOGIN_EXITOSO)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_JSON)
                                .schema(schemaBuilder().implementation(Object.class))))
                .response(responseBuilder().responseCode("401").description(MSG_401)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_PROBLEM_JSON)
                                .schema(schemaBuilder().implementation(ProblemDetail.class))))
                .response(responseBuilder().responseCode("500").description(MSG_500)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_PROBLEM_JSON)
                                .schema(schemaBuilder().implementation(ProblemDetail.class))));
    }

    public Builder validarDatosUsuario(Builder builder) {
        return builder
                .operationId("validarDatosUsuario")
                .description(DESC_VALIDAR_DATOS_USUARIO)
                .tag(TAG_USUARIOS)
                .requestBody(requestBodyBuilder()
                        .required(true)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_JSON)
                                .schema(schemaBuilder().implementation(ClienteValidationRequest.class))))
                .response(responseBuilder().responseCode("200").description(MSG_DATOS_VALIDADOS))
                .response(responseBuilder().responseCode("400").description(MSG_400)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_PROBLEM_JSON)
                                .schema(schemaBuilder().implementation(ProblemDetail.class))))
                .response(responseBuilder().responseCode("500").description(MSG_500)
                        .content(contentBuilder().mediaType(MEDIA_TYPE_PROBLEM_JSON)
                                .schema(schemaBuilder().implementation(ProblemDetail.class))));
    }

    public static OpenAPI createApiInfo(String title, String description, String version,
                                        String contactName, String contactEmail) {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail)));
    }
}
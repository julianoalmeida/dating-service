package br.com.dating.api.config;

import br.com.dating.api.message.ResponseError;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
@SecurityRequirement(name = "bearerAuth")
public class SpringDocOpenAPIConfig {

    public static final String X_API_VERSION = "X-API-Version";
    public static final String ERROR_MESSAGE = "An exception occurred when creating user, check the response body for more details";

    @Bean
    public OpenAPI openApi(OpenApiParams params) {
        return new OpenAPI().info(getInfo(params));
    }

    @Bean
    public OpenApiCustomizer errorSchemaCustomizer() {
        var errorSchema = ModelConverters.getInstance()
            .readAllAsResolvedSchema(new AnnotatedType(ResponseError.class));
        var messageSchema = ModelConverters.getInstance()
            .readAllAsResolvedSchema(new AnnotatedType(ResponseError.Message.class));
        var detailSchema = ModelConverters.getInstance()
            .readAllAsResolvedSchema(new AnnotatedType(ResponseError.MessageErrorDetail.class));
        return openApi -> openApi
            .schema(errorSchema.schema.getName(), errorSchema.schema)
            .schema(messageSchema.schema.getName(), messageSchema.schema)
            .schema(detailSchema.schema.getName(), detailSchema.schema);
    }

    @Bean
    public OpenApiCustomizer customerGlobalHeaderOpenApiCustomizer() {
        return openApi -> openApi.getPaths()
            .values()
            .forEach(pathItem -> pathItem.readOperations().forEach(operation -> addResponses(openApi, operation)));
    }

    private Info getInfo(OpenApiParams params) {
        return new Info()
            .title(params.getTitle())
            .description(params.getDescription())
            .contact(
                new Contact()
                    .name(params.getTeam())
                    .email(params.getEmail())
                    .url(params.getUrl()));
    }

    private void addResponses(OpenAPI openApi, Operation operation) {
        ApiResponses apiResponses = operation
            .getResponses()
            .addApiResponse(
                "400",
                new ApiResponse().description(ERROR_MESSAGE)
                    .content(getContent(openApi))
            )
            .addApiResponse(
                "500",
                new ApiResponse().description(ERROR_MESSAGE)
                    .content(getContent(openApi))
            );
    }

    private Content getContent(OpenAPI openApi) {
        return new Content().addMediaType(APPLICATION_JSON_VALUE, getSchema(openApi));
    }

    private MediaType getSchema(OpenAPI openApi) {
        return new MediaType()
            .schema(openApi.getComponents().getSchemas().get(ResponseError.class.getSimpleName()));
    }
}

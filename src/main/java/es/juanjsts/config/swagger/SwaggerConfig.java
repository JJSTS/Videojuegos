package es.juanjsts.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {
    @Value("${API_VERSION:v1}")
    private String apiVersion;

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API REST Gestión de Videojuegos Spring Boot DAW 2025/2026")
                                .version("1.0.0")
                                .description("API de ejemplo del curso Desarrollo de un API REST con Spring Boot para 2º DAW. 2025/20226")
                                .termsOfService("https://juanjsts.dev/doc/locense/")
                                .license(
                                        new License()
                                                .name("CC BY-NC-SA 4.0")
                                                .url("https://juanjsts.dev/doc/locense/")
                                )
                                .contact(
                                        new Contact()
                                                .name("Juan Trejo Salinas")
                                                .email("jjsts6@educa.madrid.org")
                                                .url("https://juanjsts.dev")
                                )
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Documentación del proyecto")
                                .url("https://github.com/jjsts/Videojuegos")
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("GitHub del proyecto")
                                .url("https://github.com/jjsts/Videojuegos")
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()));
    }

    @Bean
    GroupedOpenApi httpApi() {
        return GroupedOpenApi.builder()
                .group("http")
                .pathsToMatch("/api/" + apiVersion + "/videojuegos/**")
                .displayName("API Gestión de Videojuegos Spring Boot DAW 2025/2026")
                .build();
    }
}

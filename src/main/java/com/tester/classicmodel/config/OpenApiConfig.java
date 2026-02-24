package com.tester.classicmodel.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI codeAnalyzerApiDocs() {
        return new OpenAPI()
                .info(new Info().title("Classicmodel API")
                        .description("APIs for classicmodel Database")
                        .version("v1.0.0"));
    }
}

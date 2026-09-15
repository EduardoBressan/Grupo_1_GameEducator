package com.example.grupo_1_gameeducator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Camada: CONFIGURACAO. Documentacao OpenAPI / Swagger.
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Grupo 1 - GameEducator")
                .version("1.0")
                .description("Educacao Continuada Gamificada - AC1 de DevOps e QA (TDD/BDD/ATDD). "
                        + "US escolhida: ao concluir um curso com media acima de 7,0 o aluno "
                        + "ganha o direito de realizar mais 3 cursos.")
                .contact(new Contact().name("Grupo 1")));
    }
}

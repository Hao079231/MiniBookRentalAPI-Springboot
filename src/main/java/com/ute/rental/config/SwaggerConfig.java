package com.ute.rental.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Mini book rental API")
            .version("1.0")
            .description("API documentation with Swagger 3 / OpenAPI 3")
            .license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }
}

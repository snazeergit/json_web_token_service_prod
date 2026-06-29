package com.wipro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
You do not need this bean for your production application to function.
You only need it if you expose Swagger/OpenAPI documentation in production.

It does not:

* Secure your REST APIs.
* Validate JWTs.
* Configure Spring Security.
* Affect authentication or authorization.

It only tells Swagger UI:

* There is a Bearer Authentication scheme.
* Show the Authorize button.
* Include the JWT token in requests made from Swagger UI.
 */

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .schemaRequirement(
                        "Bearer Authentication",
                        new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));
    }
}

package com.ecohome.infrastructure.entrypoint;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class RouterConfig {

    private final ProductHandler productHandler;
    private final AuthHandler authHandler;

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return RouterFunctions.route()
                // Auth
                .POST("/api/auth/signup", authHandler::signup)
                .POST("/api/auth/login",  authHandler::login)
                // Products CRUD
                .GET("/api/products",             productHandler::getAll)
                .GET("/api/products/{id}",        productHandler::getById)
                .POST("/api/products",            productHandler::create)
                .PUT("/api/products/{id}",        productHandler::update)
                .PATCH("/api/products/{id}",      productHandler::patch)
                .DELETE("/api/products/{id}",     productHandler::delete)
                .build();
    }
}

package com.ecohome;

import com.ecohome.domain.model.gateways.ProductRepository;
import com.ecohome.domain.model.gateways.UserRepository;
import com.ecohome.infrastructure.entrypoint.JwtService;
import com.ecohome.usecase.createproduct.CreateProductUseCase;
import com.ecohome.usecase.deleteproduct.DeleteProductUseCase;
import com.ecohome.usecase.getproducts.GetProductsUseCase;
import com.ecohome.usecase.loginuser.LoginUserUseCase;
import com.ecohome.usecase.registeruser.RegisterUserUseCase;
import com.ecohome.usecase.updateproduct.UpdateProductUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UseCasesConfig {

    @Bean
    public GetProductsUseCase getProductsUseCase(ProductRepository productRepository) {
        return new GetProductsUseCase(productRepository);
    }

    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepository productRepository) {
        return new CreateProductUseCase(productRepository);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepository productRepository) {
        return new UpdateProductUseCase(productRepository);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepository productRepository) {
        return new DeleteProductUseCase(productRepository);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        // Adaptamos Spring's PasswordEncoder al puerto del use-case
        RegisterUserUseCase.PasswordEncoder port = passwordEncoder::encode;
        return new RegisterUserUseCase(userRepository, port);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        LoginUserUseCase.PasswordEncoder pwPort = passwordEncoder::matches;
        LoginUserUseCase.TokenGenerator  tkPort = jwtService::generateToken;
        return new LoginUserUseCase(userRepository, pwPort, tkPort);
    }
}

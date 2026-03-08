package com.ecohome.usecase.createproduct;

import com.ecohome.domain.model.Product;
import com.ecohome.domain.model.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> execute(Product product) {
        return validateProduct(product)
                .flatMap(productRepository::save);
    }

    private Mono<Product> validateProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            return Mono.error(new IllegalArgumentException("El campo 'name' es obligatorio"));
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return Mono.error(new IllegalArgumentException("El campo 'price' debe ser mayor a 0"));
        }
        return Mono.just(product);
    }
}

package com.ecohome.usecase.updateproduct;

import com.ecohome.domain.model.Product;
import com.ecohome.domain.model.exceptions.ProductNotFoundException;
import com.ecohome.domain.model.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class UpdateProductUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> execute(String id, Product product) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return Mono.error(new ProductNotFoundException(id));
        }

        return validateProduct(product)
                .flatMap(p -> productRepository.update(uuid, p))
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)));
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

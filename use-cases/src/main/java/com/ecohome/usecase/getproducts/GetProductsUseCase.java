package com.ecohome.usecase.getproducts;

import com.ecohome.domain.model.Product;
import com.ecohome.domain.model.exceptions.ProductNotFoundException;
import com.ecohome.domain.model.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class GetProductsUseCase {

    private final ProductRepository productRepository;

    public Flux<Product> findAll() {
        return productRepository.findAll();
    }

    public Mono<Product> findById(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return Mono.error(new ProductNotFoundException(id));
        }
        return productRepository.findById(uuid)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)));
    }
}

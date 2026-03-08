package com.ecohome.usecase.deleteproduct;

import com.ecohome.domain.model.exceptions.ProductNotFoundException;
import com.ecohome.domain.model.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class DeleteProductUseCase {

    private final ProductRepository productRepository;

    public Mono<Void> execute(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return Mono.error(new ProductNotFoundException(id));
        }

        return productRepository.findById(uuid)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .flatMap(p -> productRepository.deleteById(uuid));
    }
}

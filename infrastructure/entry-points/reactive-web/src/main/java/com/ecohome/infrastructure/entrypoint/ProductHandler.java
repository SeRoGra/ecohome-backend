package com.ecohome.infrastructure.entrypoint;

import com.ecohome.domain.model.Product;
import com.ecohome.domain.model.exceptions.ProductNotFoundException;
import com.ecohome.usecase.createproduct.CreateProductUseCase;
import com.ecohome.usecase.deleteproduct.DeleteProductUseCase;
import com.ecohome.usecase.getproducts.GetProductsUseCase;
import com.ecohome.usecase.updateproduct.UpdateProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final GetProductsUseCase getProductsUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    // GET /api/products
    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .body(getProductsUseCase.findAll(), Product.class);
    }

    // GET /api/products/:id
    public Mono<ServerResponse> getById(ServerRequest request) {
        String id = request.pathVariable("id");
        return getProductsUseCase.findById(id)
                .flatMap(p -> ServerResponse.ok().bodyValue(p))
                .onErrorResume(ProductNotFoundException.class, e ->
                        ServerResponse.notFound().build());
    }

    // POST /api/products
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(ProductRequest.class)
                .flatMap(body -> {
                    Product product = Product.builder()
                            .name(body.name())
                            .price(body.price())
                            .stock(body.stock() != null ? body.stock() : 0)
                            .available(true)
                            .build();
                    return createProductUseCase.execute(product);
                })
                .flatMap(p -> ServerResponse.status(201).bodyValue(p))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(Map.of("error", e.getMessage())));
    }

    // PUT /api/products/:id
    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(ProductRequest.class)
                .flatMap(body -> {
                    Product product = Product.builder()
                            .name(body.name())
                            .price(body.price())
                            .stock(body.stock())
                            .available(body.available())
                            .build();
                    return updateProductUseCase.execute(id, product);
                })
                .flatMap(p -> ServerResponse.ok().bodyValue(p))
                .onErrorResume(ProductNotFoundException.class, e ->
                        ServerResponse.notFound().build())
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(Map.of("error", e.getMessage())));
    }

    // PATCH /api/products/:id  (actualización parcial)
    public Mono<ServerResponse> patch(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(ProductPatchRequest.class)
                .flatMap(body -> getProductsUseCase.findById(id)
                        .flatMap(existing -> {
                            Product updated = Product.builder()
                                    .name(body.name()      != null ? body.name()      : existing.getName())
                                    .price(body.price()    != null ? body.price()     : existing.getPrice())
                                    .stock(body.stock()    != null ? body.stock()     : existing.getStock())
                                    .available(body.available() != null ? body.available() : existing.getAvailable())
                                    .build();
                            return updateProductUseCase.execute(id, updated);
                        })
                )
                .flatMap(p -> ServerResponse.ok().bodyValue(p))
                .onErrorResume(ProductNotFoundException.class, e ->
                        ServerResponse.notFound().build())
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(Map.of("error", e.getMessage())));
    }

    // DELETE /api/products/:id
    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return deleteProductUseCase.execute(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(ProductNotFoundException.class, e ->
                        ServerResponse.notFound().build());
    }

    record ProductRequest(String name, BigDecimal price, Integer stock, Boolean available) {}
    record ProductPatchRequest(String name, BigDecimal price, Integer stock, Boolean available) {}
}

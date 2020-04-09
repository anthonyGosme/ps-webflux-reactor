package com.wirecoffe.productapiannotation.controller;

import com.wirecoffe.productapiannotation.model.Product;
import com.wirecoffe.productapiannotation.model.ProductEvent;
import com.wirecoffe.productapiannotation.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/products")
public class ProductController {
  private ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @GetMapping
  public Flux<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @GetMapping("{id}")
  public Mono<ResponseEntity<Product>> getProduct(@PathVariable String id) {
    return productRepository
        .findById(id)
        .map(product -> ResponseEntity.ok(product))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Product> saveProduct(@RequestBody Product product) {
    return productRepository.save(product);
  }

  @PutMapping("{id}")
  public Mono<ResponseEntity<Product>> updateProduct(
      @PathVariable String id, @RequestBody Product product) {
    return productRepository
        .findById(id)
        .flatMap( // substitute by a new updated mono
            existingProduct -> {
              existingProduct.setName(product.getName());
              existingProduct.setPrice(product.getPrice());
              return productRepository.save(existingProduct);
            })
        .map(UpdatedProduct -> ResponseEntity.ok(UpdatedProduct))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @DeleteMapping("{id}")
  public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String id) {
    return productRepository
        .findById(id)
        .flatMap(
            existingProduct ->
                productRepository
                    .delete(existingProduct)
                    .then(Mono.just(ResponseEntity.ok().<Void>build())))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @DeleteMapping
  public Mono<Void> deleteProducts() {
    return productRepository.deleteAll();
  }

  @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ProductEvent> getProductEvents() {
    return Flux.interval(Duration.ofMillis(1000))
        .map(
            val -> {
              System.out.println(val);
              return new ProductEvent(val, "Product Event");
            });
  }
}

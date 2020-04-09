package com.wirecoffe.client;

import com.wirecoffe.productapiannotation.model.Product;
import com.wirecoffe.productapiannotation.model.ProductEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class WebClientAPI {

  private WebClient webClient;


  public static void main(String[] args) {
    SpringApplication.run(WebClientAPI.class, args);
  }

  WebClientAPI() {
    // this.webClient = WebClient.create("http://localhost:8080/products");
    this.webClient = WebClient.builder().baseUrl("http://localhost:8080/products").build();
  }

  @Bean
  CommandLineRunner init() {
    return args -> {
      WebClientAPI api = new WebClientAPI();

      api.postNewProduct()
              .thenMany(api.getAllProducts())
              .take(1)
              .flatMap(p -> api.updateProduct(p.getId(), "White Tea version 2", 0.99))
             // .flatMap(p -> api.deleteProduct(p.getId()))
              .thenMany(api.getAllProducts())
                  .thenMany(api.getAllEvents())
              .subscribe(System.out::println, null, null, s -> s.request(10));
    };
  }

  private Mono<ResponseEntity<Product>> postNewProduct() {
    return webClient
            .post()
            .body(Mono.just(new Product(null, "Black Tea", 1.99)), Product.class)
            .exchange()
            .flatMap(response -> response.toEntity(Product.class))
            .doOnSuccess(o -> System.out.println("**********POST " + o))
            .doOnError(o -> System.out.println("**********error " + o));
  }

  private Flux<Product> getAllProducts() {
    return webClient
            .get()
            .retrieve()
            .bodyToFlux(Product.class)
            .doOnNext(o -> System.out.println("**********GET: " + o));
  }

  private Mono<Product> updateProduct(String id, String name, double price) {
    return webClient
            .put()
            .uri("/{id}", id)
            .body(Mono.just(new Product(null, name, price)), Product.class)
            .retrieve()
            .bodyToMono(Product.class)
            .doOnSuccess(o -> System.out.println("**********UPDATE " + o));
  }

  private Mono<Void> deleteProduct(String id) {
    return webClient
            .delete()
            .uri("/{id}", id)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(o -> System.out.println("**********DELETE " + o));
  }

  private Flux<ProductEvent> getAllEvents() {
    return webClient.get().uri("/events").retrieve().bodyToFlux(ProductEvent.class);
  }


}

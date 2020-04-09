package com.wirecoffe.productapiannotation;

import com.wirecoffe.productapiannotation.handler.ProductHandler;
import com.wirecoffe.productapiannotation.model.Product;
import com.wirecoffe.productapiannotation.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class ProductApiAnnotationApplication {
  public static void main(String[] args) {
    SpringApplication.run(ProductApiAnnotationApplication.class, args);
  }

  @Bean
  CommandLineRunner init(ProductRepository repository) {
    return args -> {
      Flux.just(
              new Product(null, "Big Latte", 2.99),
              new Product(null, "Big Decaf", 2.49),
              new Product(null, "Green Tea", 1.99))
          .flatMap(repository::save)
          .thenMany(repository.findAll())
          .subscribe(System.out::println);
    };
  }

  @Bean
  RouterFunction<ServerResponse> routes(ProductHandler handler) {

    /*  return route(GET("/hproducts").and(accept(APPLICATION_JSON)), handler::getAllProducts)
          .andRoute(POST("/hproducts").and(contentType(APPLICATION_JSON)), handler::saveProduct)
          .andRoute(DELETE("/hproducts").and(accept(APPLICATION_JSON)), handler::deleteAllProducts)
          .andRoute(
              GET("/hproducts/events").and(accept(TEXT_EVENT_STREAM)), handler::getProductEvents)
          .andRoute(GET("/hproducts/{id}").and(accept(APPLICATION_JSON)), handler::getProduct)
          .andRoute(
              PUT("/hproducts/{id}").and(contentType(APPLICATION_JSON)), handler::updateProduct);
    }*/

    return nest(
        path("/hproducts"),
        nest(
            accept(APPLICATION_JSON)
                .or(contentType(APPLICATION_JSON))
                .or(accept(TEXT_EVENT_STREAM)),
            route(GET("/"), handler::getAllProducts)
                .andRoute(method(HttpMethod.POST), handler::saveProduct)
                .andRoute(DELETE("/"), handler::deleteAllProducts)
                .andRoute(GET("/events"), handler::getProductEvents)
                .andNest(
                    path("/{id}"),
                    route(method(HttpMethod.GET), handler::getProduct)
                        .andRoute(method(HttpMethod.PUT), handler::updateProduct)
                        .andRoute(method(HttpMethod.DELETE), handler::deleteProduct))));
  }
}

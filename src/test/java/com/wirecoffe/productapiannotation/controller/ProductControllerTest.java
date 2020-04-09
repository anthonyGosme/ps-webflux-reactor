package com.wirecoffe.productapiannotation.controller;

import com.wirecoffe.productapiannotation.model.Product;
import com.wirecoffe.productapiannotation.model.ProductEvent;
import com.wirecoffe.productapiannotation.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class) //junit5
//@RunWith(SpringRunner.class) // junit 4
@SpringBootTest
class ProductControllerTest {
  private WebTestClient client;

  private List<Product> expectedList;

  @Autowired private ProductRepository repository;

  @Autowired private ApplicationContext context;

  @BeforeEach
  void beforeEach() {
    this.client =
        WebTestClient.bindToApplicationContext(context)
            .configureClient()
            .baseUrl("/products")
            .build();

    this.expectedList = repository.findAll().collectList().block();// collectList().block() to stop the flux before continue
  }

  @Test
  void testGetAllProducts() {
    client
            .get()
            .uri("/")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Product.class)
            .isEqualTo(expectedList);
    assertNotNull(1);
  }

  @Test
  void testProductInvalidIdNotFound() {
    client
            .get()
            .uri("/aaa")
            .exchange()
            .expectStatus()
            .isNotFound();
  }

  @Test
  void testProductIdFound() {
    Product expectedProduct = expectedList.get(0);
    client
            .get()
            .uri("/{id}", expectedProduct.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Product.class)
            .isEqualTo(expectedProduct);
  }

  @Test
  void testProductEvents() {
    ProductEvent expectedEvent =
            new ProductEvent(0L, "Product Event");

    FluxExchangeResult<ProductEvent> result =
            client.get().uri("/events")
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(ProductEvent.class);

    StepVerifier.create(result.getResponseBody())
            .expectNext(expectedEvent)
            .expectNextCount(2)

            .consumeNextWith(event ->
                    assertEquals(Long.valueOf(3), event.getEventId()))
            .thenCancel()
            .verify();
  }
}
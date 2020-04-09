import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class OperatorTest {

  @Test
  void map() {
    Flux.range(1, 5).map(i -> i * 10).subscribe(System.out::println);
  }

  @Test
  void flatMap() {
    Flux.range(1, 4).flatMap(i -> Flux.range(i * 10, 3)).subscribe(System.out::println);
  }

  @Test
  void flatMapMany() {
    Mono.just(3).flatMapMany(i -> Flux.range(0, i)).subscribe(System.out::println);
  }

  @Test // sequential
  void concat() throws InterruptedException {
    Flux<Integer> oneToFive = Flux.range(1, 10).delayElements(Duration.ofMillis(200));
    Flux<Integer> sixToTen = Flux.range(11, 10).delayElements(Duration.ofMillis(600));
    Flux.concat(oneToFive, sixToTen).subscribe(System.out::println);
    Thread.sleep(8000);
  }

  @Test // in parallel
  void merge() throws InterruptedException {
    Flux<Integer> oneToFive = Flux.range(1, 10).delayElements(Duration.ofMillis(200));
    Flux<Integer> sixToTen = Flux.range(11, 10).delayElements(Duration.ofMillis(600));
    Flux.merge(oneToFive, sixToTen).subscribe(System.out::println);
    Thread.sleep(8000);
  }

  @Test // zip
  void zip() throws InterruptedException {
    Flux<Integer> oneToFive = Flux.range(1, 10);
    Flux<Integer> sixToTen = Flux.range(101, 10);
    Flux.zip(oneToFive, sixToTen, (i, j) -> i + ": " + j).subscribe(System.out::println);
  }
}

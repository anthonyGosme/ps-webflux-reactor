import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;

public class FluxTest {
  @Test
  void monoWithCunsumer() {
    Flux.just("A", "B", "C").log().subscribe();
    Flux.fromIterable(Arrays.asList("A", "B", "C")).log().subscribe();
  }

  @Test
  void monoFromRange() {
    Flux.range(10, 5).log().subscribe();
  }

  @Test
  void monoFromInterval() throws InterruptedException {
    Flux.interval(Duration.ofSeconds(1)).log().take(2).subscribe();
    Thread.sleep(5000);
  }

  @Test
 // unsuscribe apres 3
  void fluxRequest() {
    Flux.range(1, 5).log().subscribe(null, null, null, s -> s.request(3));
  }

  @Test
  // envoie par lot de 3
  void fluxlimit() {
    Flux.range(1, 5).log().limitRate(3).subscribe();
  }
}

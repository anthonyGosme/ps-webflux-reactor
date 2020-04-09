import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoTest {

  // private static Logger logger = LoggerFactory.getLogger(MonoTest.class);
  @Test
  void firstMono() {
    Mono.just("Asssss").log().subscribe();
  }

  @Test
  void monoWithCunsumer() {
    Mono.just("Asssss").log().subscribe(System.out::println);
  }

  @Test
  void monoDoOn() {
    Mono.just("Asssss")
        .log()
        .doOnSubscribe(s -> System.out.println("Subscribed: " + s))
        .doOnRequest(s -> System.out.println("Request: " + s))
        .doOnSuccess(s -> System.out.println("Complete: " + s))
        .subscribe(System.out::println);
    ;
  }

  @Test
  void emptyMono() {
    Mono.empty().log().subscribe(System.out::println);
  }

  @Test
  void emptyCompleteConsumeMono() {
    Mono.empty().log().subscribe(System.out::println, null, () -> System.out.println("Done"));
  }

  @Test
  void errorExceptionMono() {
    Mono.error(new RuntimeException()).log().subscribe();
  }

  @Test
  void errorDoOn() {
    Mono.error(new Exception())
        .log()
        .doOnError(s -> System.out.println("Caught: " + s))
        .subscribe();
    ;
  }

  @Test
  void errorCatchConsumerMono() {
    Mono.error(new Exception())
        .onErrorResume(
            s -> {
              System.out.println("Caught: " + s);
              return Mono.just("b");
            })
        .log()
        .subscribe();
    ;
  }

  @Test
  void errorOnErrorReturn() {
    Mono.error(new Exception())
            .log()
            .onErrorReturn("B")
            .subscribe();
    ;
  }
}

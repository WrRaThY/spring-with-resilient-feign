package priv.rdo;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = FailingClient.CLIENT_NAME, url = "http://this.does.not.exist")
public interface FailingClient {
    String CLIENT_NAME = "FailingClient";

    @Retry(name = FailingClient.CLIENT_NAME)
    @CircuitBreaker(name = FailingClient.CLIENT_NAME)
    @GetMapping(value = "/resources/titles/{bookId}")
    Book getById(
            @PathVariable("bookId") String bookId
    );
}
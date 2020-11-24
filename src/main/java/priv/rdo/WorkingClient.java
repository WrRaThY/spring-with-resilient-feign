package priv.rdo;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = WorkingClient.CLIENT_NAME, url = "https://reststop.randomhouse.com")
public interface WorkingClient {
    String CLIENT_NAME = "WorkingClient";

    @Retry(name = WorkingClient.CLIENT_NAME)
    @CircuitBreaker(name = WorkingClient.CLIENT_NAME)
    @GetMapping(value = "/resources/titles/{bookId}", headers = {HttpHeaders.ACCEPT + "=" + MimeTypeUtils.APPLICATION_JSON_VALUE})
    Book getById(
            @PathVariable("bookId") String bookId
    );
}

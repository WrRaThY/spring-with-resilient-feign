package priv.rdo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "books")
public class ExampleController {
    private final WorkingClient workingClient;
    private final FailingClient failingClient;

    public ExampleController(WorkingClient workingClient, FailingClient failingClient) {
        this.workingClient = workingClient;
        this.failingClient = failingClient;
    }

    @GetMapping(path = "working")
    public Book workingBook() {
        Book book = workingClient.getById("9781400079148");

        return book;
    }

    @GetMapping(path = "failing")
    public Book failingBook() {
        Book book = failingClient.getById("9781400079148");

        return book;
    }
}

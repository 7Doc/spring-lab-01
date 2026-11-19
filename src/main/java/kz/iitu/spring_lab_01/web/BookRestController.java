package kz.iitu.spring_lab_01.web;

import jakarta.validation.Valid;
import kz.iitu.spring_lab_01.catalog.Book;
import kz.iitu.spring_lab_01.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService service;

    public BookRestController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<Book> list(
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "10") int limit) {

        return service.findAll(author).stream()
                .limit(Math.max(0, limit))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> find(@PathVariable long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> create(@Valid @RequestBody Book book) {
        Book saved = service.create(
                new Book(null, book.title(), book.author(), book.year())
        );

        return ResponseEntity
                .created(URI.create("/api/books/" + saved.id()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> replace(
            @PathVariable long id,
            @Valid @RequestBody Book book) {

        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Book updated = service.create(
                new Book(id, book.title(), book.author(), book.year())
        );

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(headers = "Accept-Language")
    public List<Map<String, Object>> listLocalized(
            @RequestHeader(value = "Accept-Language",
                    defaultValue = "en") String language,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "10") int limit) {

        boolean russian = language.toLowerCase().startsWith("ru");

        return service.findAll(author).stream()
                .limit(Math.max(0, limit))
                .map(book -> {
                    Map<String, Object> item = new LinkedHashMap<>();

                    if (russian) {
                        item.put("идентификатор", book.id());
                        item.put("название", book.title());
                        item.put("автор", book.author());
                        item.put("год", book.year());
                    } else {
                        item.put("id", book.id());
                        item.put("title", book.title());
                        item.put("author", book.author());
                        item.put("year", book.year());
                    }

                    return item;
                })
                .toList();
    }
}
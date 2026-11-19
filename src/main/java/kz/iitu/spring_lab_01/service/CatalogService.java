package kz.iitu.spring_lab_01.service;

import kz.iitu.spring_lab_01.audit.Audited;
import kz.iitu.spring_lab_01.catalog.Book;
import kz.iitu.spring_lab_01.catalog.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class CatalogService {

    @Autowired
    @Lazy
    private CatalogService self;

    private final BookRepository bookRepository;

    public CatalogService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public String findById(long id) {
        sleep(50);
        return "Item no. " + id;
    }

    @Audited(action = "CATALOG_LIST", logArguments = true)
    public List<String> findAll(int limit) {
        sleep(300);
        return IntStream.rangeClosed(1, limit)
                .mapToObj(i -> "Item no. " + i)
                .toList();
    }

    @Audited(action = "CATALOG_REMOVE")
    public String remove(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Invalid identifier: " + id);
        }
        return "Removed item no. " + id;
    }


    public List<Book> search(String titlePart) {
        if (titlePart == null || titlePart.isBlank()) {
            throw new IllegalArgumentException("Search query must not be blank");
        }
        String needle = titlePart.toLowerCase();
        return bookRepository.findAll().stream()
                .filter(b -> b.title().toLowerCase().contains(needle))
                .toList();
    }

    public String removeTwice(long id) {
        String first = self.remove(id);
        String second = self.remove(id + 1);
        return first + "; " + second;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
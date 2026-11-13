package kz.iitu.spring_lab_01.service;

import kz.iitu.spring_lab_01.catalog.Book;
import kz.iitu.spring_lab_01.catalog.BookRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll(String author) {
        if (author == null || author.isBlank()) {
            return repository.findAll();
        }

        return repository.findAll().stream()
                .filter(book -> book.author()
                        .toLowerCase()
                        .contains(author.trim().toLowerCase()))
                .toList();
    }

    public Optional<Book> findById(long id) {
        return repository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Book create(Book book) {
        return repository.save(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete(long id) {
        return repository.deleteById(id);
    }
}
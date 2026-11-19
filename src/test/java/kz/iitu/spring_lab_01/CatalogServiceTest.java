package kz.iitu.spring_lab_01.service;

import kz.iitu.spring_lab_01.catalog.Book;
import kz.iitu.spring_lab_01.catalog.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    CatalogService service;

    //Happy path
    @Test
    void searchReturnsBooksWhoseTitleMatchesTheQuery() {
        given(bookRepository.findAll()).willReturn(List.of(
                new Book(1L, "Effective Java", "Joshua Bloch", 2018),
                new Book(2L, "Clean Code", "Robert Martin", 2008),
                new Book(3L, "Spring in Action", "Craig Walls", 2022)
        ));

        List<Book> result = service.search("Java");

        assertThat(result).extracting(Book::title)
                .containsExactly("Effective Java");
    }

    //Not-found path
    @Test
    void searchWithBlankQueryThrowsException() {
        assertThatThrownBy(() -> service.search("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    //Interaction check
    @Test
    void searchCallsRepositoryOnce() {
        given(bookRepository.findAll()).willReturn(List.of());

        service.search("Dune");

        verify(bookRepository).findAll();
        verifyNoMoreInteractions(bookRepository);
    }

    //Ветка варианта 8: поиск без результатов
    @Test
    void searchWithNoMatchesReturnsEmptyList() {
        given(bookRepository.findAll()).willReturn(List.of(
                new Book(1L, "Effective Java", "Joshua Bloch", 2018),
                new Book(2L, "Clean Code", "Robert Martin", 2008)
        ));

        List<Book> result = service.search("Dune");

        assertThat(result).isEmpty();
    }

    //Закрывает жёлтую ветку
    @Test
    void searchWithNullQueryThrowsException() {
        assertThatThrownBy(() -> service.search(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }
}
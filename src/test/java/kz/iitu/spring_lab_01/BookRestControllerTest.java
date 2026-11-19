package kz.iitu.spring_lab_01;

import kz.iitu.spring_lab_01.catalog.Book;
import kz.iitu.spring_lab_01.service.BookService;
import kz.iitu.spring_lab_01.web.BookRestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BookService service;

    // 1. GET существующей книги -> 200 + JSON-поле
    @Test
    void existingBookIsReturnedWithItsTitle() throws Exception {
        given(service.findById(1L))
                .willReturn(Optional.of(new Book(1L, "Dune", "Herbert", 1965)));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dune"))
                .andExpect(jsonPath("$.author").value("Herbert"));
    }

    // 2. GET несуществующей книги -> 404
    @Test
    void missingBookBecomes404() throws Exception {
        given(service.findById(7L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/books/7"))
                .andExpect(status().isNotFound());
    }

    // 3. POST с невалидным телом -> 400 + problem detail
    @Test
    void blankTitleOnCreateBecomes400() throws Exception {
        String invalidBody = """
                {"title":"","author":"Herbert","year":1965}
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists());
    }
}
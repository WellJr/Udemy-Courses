package com.cursosudemy.libraryapi.service;

import com.cursosudemy.libraryapi.exception.BusinessException;
import com.cursosudemy.libraryapi.model.entity.Book;
import com.cursosudemy.libraryapi.model.repository.BookRepository;
import com.cursosudemy.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// create a mini context of injection dependencies to run the tests
@ExtendWith(SpringExtension.class)

// run with test profile / environment
@ActiveProfiles("test")

public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        //cenario
        Book book = createValidBook();

        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Book result = Book.builder().id(11L).isbn("123").author("Fulano").title("As aventuras").build();

        //acao
        Mockito.when(repository.save(book)).thenReturn(result);

        Book savedBook = service.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");

    }

    @Test
    @DisplayName("Deve lança erro de negocio ao tentar salvar um livro duplicado")
    public void shouldNotSaveBookWithDuplicatedISBN() {
        // cenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //acao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        //verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getByIdTest() {
        Long id = 1L;

        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execução
        Optional<Book> foundBook = service.getById(id);

        //verificação
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe ana base")
    public void bookNotFoundByIdTest() {
        Long id = 1L;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execução
        Optional<Book> book = service.getById(id);

        //verificação
        assertThat(book.isPresent()).isFalse();
    }


    @Test
    @DisplayName("Deve deletar um livro com sucesso")
    public void deleteBookTest() {
        //cenario
        Book bookToDelete = createValidBook();
        bookToDelete.setId(1L);

        //ação
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(bookToDelete));

        //verificação
        Mockito.verify(repository, Mockito.times(1)).delete(bookToDelete);

    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente")
    public void deleteInvalidBookTest() {
        //cenario
        Book bookToDelete = createValidBook();

        //ação
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(bookToDelete));

        //verificação
        Mockito.verify(repository, Mockito.never()).delete(bookToDelete);
    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso")
    public void updateBookTest() {
        // cenário
        Long id = 1L;

        // livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        //simulacao
        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        // ação
        Book book = service.update(updatingBook);

        //verificação
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() {
        // cenerio
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> bookList = Arrays.asList(book);

        Page<Book> page  =  new PageImpl<Book>(bookList, pageRequest, 1);

        Mockito.when(repository.findAll( Mockito.any(Example.class), Mockito.any(PageRequest.class) ))
                .thenReturn(page);

        //ação
        Page<Book> result = service.find(book, pageRequest); //encontra na base todos os livros com as propriedades setadas no objeto book.

        //verificação
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(bookList);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente")
    public void updateInvalidBookTest() {
        //cenario
        Book bookToDelete = createValidBook();

        //ação
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(bookToDelete));

        //verificação
        Mockito.verify(repository, Mockito.never()).save(bookToDelete);
    }


    private Book createValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }
}

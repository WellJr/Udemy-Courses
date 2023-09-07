package com.cursosudemy.libraryapi.model.repository;

import com.cursosudemy.libraryapi.model.entity.Book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// create a mini context of dependencies injection to run the tests
@ExtendWith(SpringExtension.class)

@ActiveProfiles("test")

// Indicates we gonna do tests with JPA. It creates an instance an In Memory BD and after que testes delete
// all data.
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    /*
        TestEntityManager is used in the repository to execute the operations of the database (Save, update etc...)
     */
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExists () {
        // cenario
        String isbn = "123";

        Book book = Book.builder().title("As aventuras").author("Fulano").isbn(isbn).build();
        entityManager.persist(book); //persist an object into database

        // acao
        boolean exists = repository.existsByIsbn(isbn);

        //verificacao
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar FALSO quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesntExist () {
        // cenario
        String isbn = "321";

        // acao
        boolean exist = repository.existsByIsbn(isbn);

        // verificacao
        assertThat(exist).isFalse();
    }
}
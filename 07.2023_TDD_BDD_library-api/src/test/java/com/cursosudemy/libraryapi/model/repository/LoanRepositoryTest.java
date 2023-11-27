package com.cursosudemy.libraryapi.model.repository;

import com.cursosudemy.libraryapi.model.entity.Book;
import com.cursosudemy.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// create a mini context of dependencies injection to run the tests
@ExtendWith(SpringExtension.class)

@ActiveProfiles("test")

// Indicates we gonna do tests with JPA. It creates an instance an In Memory BD and after the tests delete
// all data.
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    /*
        TestEntityManager is used in the repository to execute the operations of the database (Save, update etc...)
     */
    TestEntityManager entityManager;

    @Autowired
    LoanRepository repository;

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
    public void existsByBookAndNotReturned() {
        //cenário
        Book book = createNewBook("123");
        entityManager.persist(book);

        Loan loan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        entityManager.persist(loan);

        //ação
        boolean exists = repository.existsByBookAndNotReturned(book);

        //vefificação
        assertThat(exists).isTrue();
    }

    private Book createNewBook(String isbn) {
        return Book.builder().title("As aventuras").author("Fulano").isbn(isbn).build();
    }


}

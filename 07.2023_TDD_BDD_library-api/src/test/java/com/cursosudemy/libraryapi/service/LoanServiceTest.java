package com.cursosudemy.libraryapi.service;

import com.cursosudemy.libraryapi.exception.BusinessException;
import com.cursosudemy.libraryapi.model.entity.Book;
import com.cursosudemy.libraryapi.model.entity.Loan;
import com.cursosudemy.libraryapi.model.repository.LoanRepository;
import com.cursosudemy.libraryapi.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

// create a mini context of injection dependencies to run the tests
@ExtendWith(SpringExtension.class)

// run with test profile / environment
@ActiveProfiles("test")
public class LoanServiceTest {
    @MockBean
    private LoanRepository repository;

    private LoanService service;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }


    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest() {
        //cenario
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();


        Loan savedLoan = Loan.builder()
                .id(1L)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book)
                .build();

        Mockito.when(repository.existsByBookAndNotReturned(savingLoan.getBook())).thenReturn(false);
        Mockito.when(repository.save(savingLoan)).thenReturn(savedLoan);

        //ação
        Loan loan = service.save(savingLoan);

        //verificação
        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }


    @Test
    @DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com livro já emprestado")
    public void loanedBookSavedTest() {
        //cenario
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(repository.existsByBookAndNotReturned(savingLoan.getBook()))
                .thenReturn(true);

        //ação
        Throwable exception =  catchThrowable(() -> service.save(savingLoan));

        //verificação
        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        Mockito.verify(repository, Mockito.never()).save(savingLoan);
    }

    @Test
    @DisplayName("Deve obter as informações de um empréstimo pelo ID")
    public void getLoanDetailsTest() {
        // cenario
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        // ação
        Optional<Loan> returnedLoan = service.getById(id);

        //verificação
        Assertions.assertThat(returnedLoan.isPresent()).isTrue();
        Assertions.assertThat(returnedLoan.get().getId()).isEqualTo(loan.getId());
        Assertions.assertThat(returnedLoan.get().getCustomer()).isEqualTo(loan.getCustomer());
        Assertions.assertThat(returnedLoan.get().getBook()).isEqualTo(loan.getBook());
        Assertions.assertThat(returnedLoan.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());

    }

    public Loan createLoan() {
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        return Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
    }

}

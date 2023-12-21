package com.cursosudemy.libraryapi.api.resources;

import com.cursosudemy.libraryapi.api.dto.LoanDto;
import com.cursosudemy.libraryapi.api.dto.LoanFilterDTO;
import com.cursosudemy.libraryapi.api.dto.ReturnedLoanDTO;
import com.cursosudemy.libraryapi.exception.BusinessException;
import com.cursosudemy.libraryapi.model.entity.Book;
import com.cursosudemy.libraryapi.model.entity.Loan;
import com.cursosudemy.libraryapi.service.BookService;
import com.cursosudemy.libraryapi.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// create a mini context of dependencies injections to run the tests
@ExtendWith(SpringExtension.class)

// run with test profile / environment
@ActiveProfiles("test")

/*
* The @WebMvcTest annotation is used for Spring MVC tests.
* It disables full auto-configuration and instead applies only configuration relevant to MVC tests.
* */
@WebMvcTest(LoanController.class)

// configure objects to do requests
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void createLoanTest() throws Exception {
        //cenario
        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Book book = Book.builder().id(1L).isbn("123").build();

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of( book ));

        Loan loan = Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now()).build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        //acao
        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        //resultado
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect( content().string("1") );

    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro inexistente.")
    public void invalidIsbnCreateLoanTest() throws Exception {
        //cenario
        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.empty());

        //acao
        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        //resultado
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
    }
    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro já emprestado.")
    public void loanedBookonCreateLoanTest() throws Exception {
        //cenario
        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn("123").build();

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of( book ));


        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book already loaned"));

        //acao
        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        //resultado
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"));
    }

    @Test
    @DisplayName("Deve retornar um livro")
    public void returnBookTest() throws Exception{
        // cenario { returned: true }
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Loan loan = Loan.builder().id(1L).build();

        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(loan));

        //ação
        mvc.perform(
             MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                     .accept(MediaType.APPLICATION_JSON)
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(json)
        ).andExpect(status().isOk());

        //verificação
        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar deveolver um livro inexistente.")
    public void returnInxistentBookTest() throws Exception{
        // cenario { returned: true }
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();

        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());


        String json = new ObjectMapper().writeValueAsString(dto);

        //ação
        mvc.perform(
                MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )

        //verificação
        .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtrar emprestimos")
    public void findBookTest() throws Exception{

        //cenario
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);
        Book book = Book.builder().id(1L).isbn("321").build();
        loan.setBook(book);


        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn( new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1));

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100",
                book.getIsbn(),
                loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);


        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect( MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)) )
                .andExpect( MockMvcResultMatchers.jsonPath("totalElements").value(1) )
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(10))
                .andExpect( MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
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

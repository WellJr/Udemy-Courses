package com.cursosudemy.libraryapi.api.resources;

import com.cursosudemy.libraryapi.api.dto.LoanDto;
import com.cursosudemy.libraryapi.model.entity.Book;
import com.cursosudemy.libraryapi.model.entity.Loan;
import com.cursosudemy.libraryapi.service.BookService;
import com.cursosudemy.libraryapi.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
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
}

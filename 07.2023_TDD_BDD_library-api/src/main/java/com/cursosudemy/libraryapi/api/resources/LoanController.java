package com.cursosudemy.libraryapi.api.resources;

import com.cursosudemy.libraryapi.api.dto.BookDTO;
import com.cursosudemy.libraryapi.api.dto.LoanDto;
import com.cursosudemy.libraryapi.api.dto.LoanFilterDTO;
import com.cursosudemy.libraryapi.api.dto.ReturnedLoanDTO;
import com.cursosudemy.libraryapi.model.entity.Book;
import com.cursosudemy.libraryapi.model.entity.Loan;
import com.cursosudemy.libraryapi.service.BookService;
import com.cursosudemy.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {


    private final LoanService loanService;
    private final BookService bookService;

    private final ModelMapper modelMapper;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody  LoanDto dto) {

        Book book =  bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        entity = loanService.save(entity);

        return entity.getId();
    }

    @PatchMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        Loan loan = loanService.getById(id).orElseThrow( () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found for passed id"));

        loan.setReturned(dto.getReturned());
        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDto> find(LoanFilterDTO loanFilterDTO, Pageable pageableRequest) {
        Page<Loan> result = loanService.find(loanFilterDTO, pageableRequest);

        List<LoanDto> loanDtos = result.getContent().stream().map(entity ->
            {
                Book book = entity.getBook();
                BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                LoanDto loanDto = modelMapper.map(entity, LoanDto.class);
                loanDto.setBookDTO(bookDTO);

                return loanDto;
            }).collect(Collectors.toList());

        return new PageImpl<LoanDto>(loanDtos, pageableRequest, result.getTotalElements());
    }
}

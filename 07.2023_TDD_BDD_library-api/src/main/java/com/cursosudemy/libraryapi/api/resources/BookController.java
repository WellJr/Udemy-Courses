package com.cursosudemy.libraryapi.api.resources;

import com.cursosudemy.libraryapi.api.dto.BookDTO;
import com.cursosudemy.libraryapi.api.dto.LoanDto;
import com.cursosudemy.libraryapi.api.exceptions.ApiErrors;
import com.cursosudemy.libraryapi.exception.BusinessException;
import com.cursosudemy.libraryapi.model.entity.Book;
import com.cursosudemy.libraryapi.model.entity.Loan;
import com.cursosudemy.libraryapi.service.BookService;
import com.cursosudemy.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
// @RequiredArgsConstructor

@Api("Book API")
@Slf4j
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;

    private final LoanService loanService;

    public BookController(BookService service, ModelMapper modelMapper, LoanService loanService) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.loanService = loanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates a book")
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        log.info("creating a book for isbn: {} ", dto.getIsbn());
        //Convert DTO to Entity
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping (path = "{id}")
    @ApiOperation("Obtains a book details by id")
    public BookDTO findById(@PathVariable Long id) {
        log.info("obtaining details for book id: {} ", id);
        return service.getById(id)
                .map( book -> modelMapper.map(book, BookDTO.class) )
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @ApiOperation("Find books by param")
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);

        List<BookDTO> list = result.getContent()
                .stream()
                .map( entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());


        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deletes a book by id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book successfully deleted")
    })
    public void delete(@PathVariable Long id) {
        log.info("deleting a book by id: {} ", id);
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));
        service.delete(book);
    }

    @PutMapping(path = "{id}")
    @ApiOperation("Updates a book by id")
    public BookDTO update(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        log.info("updating a book");
        return service.getById(id).map( book -> {
            book.setAuthor(bookDTO.getAuthor());
            book.setTitle(bookDTO.getTitle());
            book  = service.update(book);
            return modelMapper.map(book, BookDTO.class);

        }).orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));

    }
    @GetMapping("{id}/loans")
    @ApiOperation("Gets loans by book id")
    public Page<LoanDto> loansByBook(@PathVariable Long bookId, Pageable pageable) {
        Book book = service.getById(bookId).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoansByBook(book, pageable);

        List<LoanDto> loanDtos =result.getContent()
                .stream()
                .map(
                        loan -> {
                            Book loanBook = loan.getBook();
                            BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);

                            LoanDto loanDto = modelMapper.map(loan, LoanDto.class);
                            loanDto.setBookDTO(bookDTO);
                            return loanDto;
                        }
                ).collect(Collectors.toList());

        return new PageImpl<LoanDto>(loanDtos, pageable, result.getTotalElements());
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception) {
//        BindingResult bindingResult = exception.getBindingResult();
//
//        return new ApiErrors(bindingResult);
//
//    }
//
//    @ExceptionHandler(BusinessException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiErrors handleBusinessExceptions(BusinessException exception){
//        return new ApiErrors(exception);
//    }

}

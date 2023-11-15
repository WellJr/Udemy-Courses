package com.cursosudemy.libraryapi.api.resources;

import com.cursosudemy.libraryapi.api.dto.BookDTO;
import com.cursosudemy.libraryapi.api.exceptions.ApiErrors;
import com.cursosudemy.libraryapi.exception.BusinessException;
import com.cursosudemy.libraryapi.model.entity.Book;
import com.cursosudemy.libraryapi.service.BookService;
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
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        //Convert DTO to Entity
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping (path = "{id}")
    public BookDTO findById(@PathVariable Long id) {
        return service.getById(id)
                .map( book -> modelMapper.map(book, BookDTO.class) )
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
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
    public void delete(@PathVariable Long id) {
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));
        service.delete(book);
    }

    @PutMapping(path = "{id}")
    public BookDTO update(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        return service.getById(id).map( book -> {
            book.setAuthor(bookDTO.getAuthor());
            book.setTitle(bookDTO.getTitle());
            book  = service.update(book);
            return modelMapper.map(book, BookDTO.class);

        }).orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));

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

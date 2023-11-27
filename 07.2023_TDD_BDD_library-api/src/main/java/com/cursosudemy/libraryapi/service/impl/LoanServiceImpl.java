package com.cursosudemy.libraryapi.service.impl;

import com.cursosudemy.libraryapi.exception.BusinessException;
import com.cursosudemy.libraryapi.model.entity.Loan;
import com.cursosudemy.libraryapi.model.repository.LoanRepository;
import com.cursosudemy.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {

        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {

        if(repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book already loaned");
        }

        return repository.save(loan);
    }
}

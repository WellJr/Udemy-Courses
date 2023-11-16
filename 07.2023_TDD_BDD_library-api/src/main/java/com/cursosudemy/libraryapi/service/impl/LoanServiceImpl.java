package com.cursosudemy.libraryapi.service.impl;

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
        return repository.save(loan);
    }
}

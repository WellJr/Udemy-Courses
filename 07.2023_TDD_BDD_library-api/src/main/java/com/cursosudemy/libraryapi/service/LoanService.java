package com.cursosudemy.libraryapi.service;

import com.cursosudemy.libraryapi.model.entity.Loan;

import java.util.Map;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}

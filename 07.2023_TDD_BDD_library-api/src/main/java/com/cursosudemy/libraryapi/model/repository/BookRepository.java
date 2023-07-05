package com.cursosudemy.libraryapi.model.repository;

import com.cursosudemy.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}

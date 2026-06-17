package com.example.cache.repository;

import com.example.cache.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    void deleteByIsbn(String isbn);
}

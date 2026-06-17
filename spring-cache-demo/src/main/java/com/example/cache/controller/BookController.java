package com.example.cache.controller;

import com.example.cache.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/default/{isbn}")
    public ResponseEntity<String> getFromDefault(@PathVariable String isbn) {
        // Flag set to true for demonstration
        String book = bookService.getBookByIsbn(isbn, true).orElse("Book Not Found");
        return ResponseEntity.ok(book);
    }

    @PostMapping("/{isbn}/evict")
    public ResponseEntity<String> evict(@PathVariable String isbn) {
        bookService.evictBook(isbn);
        return ResponseEntity.ok("Evicted book cache for ISBN: " + isbn);
    }

    @GetMapping("/redis/{isbn}")
    public ResponseEntity<String> getFromRedis(@PathVariable String isbn) {
        String book = bookService.getBookFromRedis(isbn);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/caffeine/{isbn}")
    public ResponseEntity<String> getFromCaffeine(@PathVariable String isbn) {
        String book = bookService.getBookFromCaffeine(isbn);
        return ResponseEntity.ok(book);
    }
}

package com.example.cache.controller;

import com.example.cache.entity.Book;
import com.example.cache.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.ok(savedBook);
    }

    @GetMapping("/default/{isbn}")
    public ResponseEntity<?> getFromDefault(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn, true)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{isbn}/evict")
    public ResponseEntity<String> evict(@PathVariable String isbn) {
        bookService.evictBook(isbn);
        return ResponseEntity.ok("Evicted book cache and deleted from DB for ISBN: " + isbn);
    }

    @GetMapping("/redis/{isbn}")
    public ResponseEntity<Book> getFromRedis(@PathVariable String isbn) {
        Book book = bookService.getBookFromRedis(isbn);
        if (book != null) {
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/caffeine/{isbn}")
    public ResponseEntity<Book> getFromCaffeine(@PathVariable String isbn) {
        Book book = bookService.getBookFromCaffeine(isbn);
        if (book != null) {
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.notFound().build();
    }
}

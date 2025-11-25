package com.dgsw.bookice.service;

import com.dgsw.bookice.dto.request.BookCreateRequest;
import com.dgsw.bookice.dto.request.BookUpdateRequest;
import com.dgsw.bookice.dto.response.BookResponse;
import com.dgsw.bookice.entity.Book;
import com.dgsw.bookice.exception.BookNotFoundException;
import com.dgsw.bookice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BookResponse createBook(BookCreateRequest request) {
        log.info("도서 등록 요청: {}", request.getTitle());

        // ISBN 중복 검증
        if (request.getIsbn() != null && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalStateException("이미 존재하는 ISBN입니다: " + request.getIsbn());
        }

        Book book = request.toEntity();
        Book savedBook = bookRepository.save(book);

        log.info("도서 등록 완료: ID={}, 제목={}", savedBook.getId(), savedBook.getTitle());
        return BookResponse.from(savedBook);
    }

    @Override
    public BookResponse getBook(Long id) {
        log.info("도서 조회 요청: ID={}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        return BookResponse.from(book);
    }

    @Override
    public List<BookResponse> getAllBooks() {
        log.info("전체 도서 목록 조회 요청");

        List<Book> books = bookRepository.findAll();

        return books.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookResponse> searchBooks(String keyword, Pageable pageable) {
        log.info("도서 검색 요청: keyword={}, page={}, size={}",
                keyword, pageable.getPageNumber(), pageable.getPageSize());

        Page<Book> bookPage = bookRepository.searchBooks(keyword, pageable);

        return bookPage.map(BookResponse::from);
    }

    @Override
    public Page<BookResponse> searchBooksByConditions(String title, String author,
                                                      String category, Pageable pageable) {
        log.info("도서 동적 검색 요청: title={}, author={}, category={}", title, author, category);

        Page<Book> bookPage = bookRepository.searchByConditions(title, author, category, pageable);

        return bookPage.map(BookResponse::from);
    }

    @Override
    public List<BookResponse> searchByTitle(String title) {
        log.info("제목으로 도서 검색: {}", title);

        List<Book> books = bookRepository.findByTitleContaining(title);

        return books.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> searchByAuthor(String author) {
        log.info("저자로 도서 검색: {}", author);

        List<Book> books = bookRepository.findByAuthorContaining(author);

        return books.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> searchByCategory(String category) {
        log.info("카테고리로 도서 검색: {}", category);

        List<Book> books = bookRepository.findByCategory(category);

        return books.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> searchByPriceRange(Integer minPrice, Integer maxPrice) {
        log.info("가격 범위로 도서 검색: {}원 ~ {}원", minPrice, maxPrice);

        List<Book> books = bookRepository.findByPriceRange(minPrice, maxPrice);

        return books.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> getBooksInStock() {
        log.info("재고가 있는 도서 조회");

        List<Book> books = bookRepository.findBooksInStock();

        return books.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookUpdateRequest request) {
        log.info("도서 수정 요청: ID={}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.update(
                request.getTitle(),
                request.getAuthor(),
                request.getCategory(),
                request.getPublisher(),
                request.getPrice(),
                request.getDescription()
        );

        log.info("도서 수정 완료: ID={}, 제목={}", book.getId(), book.getTitle());
        return BookResponse.from(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        log.info("도서 삭제 요청: ID={}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        bookRepository.delete(book);
        log.info("도서 삭제 완료: ID={}", id);
    }

    @Override
    @Transactional
    public BookResponse increaseStock(Long id, int quantity) {
        log.info("재고 증가 요청: ID={}, 수량={}", id, quantity);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.increaseStock(quantity);

        log.info("재고 증가 완료: ID={}, 현재 재고={}", id, book.getStockQuantity());
        return BookResponse.from(book);
    }

    @Override
    @Transactional
    public BookResponse decreaseStock(Long id, int quantity) {
        log.info("재고 감소 요청: ID={}, 수량={}", id, quantity);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.decreaseStock(quantity);

        log.info("재고 감소 완료: ID={}, 현재 재고={}", id, book.getStockQuantity());
        return BookResponse.from(book);
    }
}


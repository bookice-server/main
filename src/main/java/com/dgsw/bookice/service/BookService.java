package com.dgsw.bookice.service;

import com.dgsw.bookice.dto.request.BookCreateRequest;
import com.dgsw.bookice.dto.request.BookUpdateRequest;
import com.dgsw.bookice.dto.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    /**
     * 도서 등록
     */
    BookResponse createBook(BookCreateRequest request);

    /**
     * 도서 단건 조회
     */
    BookResponse getBook(Long id);

    /**
     * 전체 도서 목록 조회
     */
    List<BookResponse> getAllBooks();

    /**
     * 도서 검색 (페이징)
     */
    Page<BookResponse> searchBooks(String keyword, Pageable pageable);

    /**
     * 동적 조건 검색 (QueryDSL, 페이징)
     */
    Page<BookResponse> searchBooksByConditions(String title, String author,
                                               String category, Pageable pageable);

    /**
     * 제목으로 검색
     */
    List<BookResponse> searchByTitle(String title);

    /**
     * 저자로 검색
     */
    List<BookResponse> searchByAuthor(String author);

    /**
     * 카테고리로 검색
     */
    List<BookResponse> searchByCategory(String category);

    /**
     * 가격 범위로 검색
     */
    List<BookResponse> searchByPriceRange(Integer minPrice, Integer maxPrice);

    /**
     * 재고가 있는 도서 조회
     */
    List<BookResponse> getBooksInStock();

    /**
     * 도서 정보 수정
     */
    BookResponse updateBook(Long id, BookUpdateRequest request);

    /**
     * 도서 삭제
     */
    void deleteBook(Long id);

    /**
     * 재고 증가
     */
    BookResponse increaseStock(Long id, int quantity);

    /**
     * 재고 감소
     */
    BookResponse decreaseStock(Long id, int quantity);
}

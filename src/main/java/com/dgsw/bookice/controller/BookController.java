package com.dgsw.bookice.controller;

import com.dgsw.bookice.dto.request.BookCreateRequest;
import com.dgsw.bookice.dto.request.BookUpdateRequest;
import com.dgsw.bookice.dto.response.ApiResponse;
import com.dgsw.bookice.dto.response.BookResponse;
import com.dgsw.bookice.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "도서 관리", description = "도서 CRUD, 검색 및 재고 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "도서 등록", description = "새로운 도서를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
            @Valid @RequestBody BookCreateRequest request) {
        log.info("POST /api/books - 도서 등록 요청");
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("도서가 성공적으로 등록되었습니다.", response));
    }

    @Operation(summary = "도서 단건 조회", description = "ID로 특정 도서의 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long id) {
        log.info("GET /api/books/{} - 도서 조회 요청", id);
        BookResponse response = bookService.getBook(id);
        return ResponseEntity.ok(ApiResponse.success("도서 조회 성공", response));
    }

    @Operation(summary = "전체 도서 목록 조회", description = "모든 도서 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        log.info("GET /api/books - 전체 도서 목록 조회 요청");
        List<BookResponse> response = bookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success("도서 목록 조회 성공", response));
    }

    @Operation(summary = "도서 키워드 검색", description = "키워드로 도서를 검색합니다. (페이징 지원)")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> searchBooks(
            @Parameter(description = "검색어") @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /api/books/search - 도서 검색 요청: keyword={}", keyword);
        Page<BookResponse> response = bookService.searchBooks(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("도서 검색 성공", response));
    }

    @Operation(summary = "도서 상세 검색 (QueryDSL)", description = "제목, 저자, 카테고리를 조합하여 검색합니다.")
    @GetMapping("/search/advanced")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> searchBooksAdvanced(
            @Parameter(description = "제목") @RequestParam(required = false) String title,
            @Parameter(description = "저자") @RequestParam(required = false) String author,
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /api/books/search/advanced - 동적 검색 요청");
        Page<BookResponse> response = bookService.searchBooksByConditions(title, author, category, pageable);
        return ResponseEntity.ok(ApiResponse.success("도서 검색 성공", response));
    }

    @Operation(summary = "제목 검색", description = "제목에 특정 단어가 포함된 도서를 검색합니다.")
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchByTitle(
            @Parameter(description = "검색할 제목", required = true) @RequestParam String title) {
        log.info("GET /api/books/search/title - 제목 검색: {}", title);
        List<BookResponse> response = bookService.searchByTitle(title);
        return ResponseEntity.ok(ApiResponse.success("제목 검색 성공", response));
    }

    @Operation(summary = "저자 검색", description = "저자 이름으로 도서를 검색합니다.")
    @GetMapping("/search/author")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchByAuthor(
            @Parameter(description = "검색할 저자명", required = true) @RequestParam String author) {
        log.info("GET /api/books/search/author - 저자 검색: {}", author);
        List<BookResponse> response = bookService.searchByAuthor(author);
        return ResponseEntity.ok(ApiResponse.success("저자 검색 성공", response));
    }

    @Operation(summary = "카테고리 검색", description = "특정 카테고리의 도서를 검색합니다.")
    @GetMapping("/search/category")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchByCategory(
            @Parameter(description = "검색할 카테고리", required = true) @RequestParam String category) {
        log.info("GET /api/books/search/category - 카테고리 검색: {}", category);
        List<BookResponse> response = bookService.searchByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("카테고리 검색 성공", response));
    }

    @Operation(summary = "가격 범위 검색", description = "최소 가격과 최대 가격 사이의 도서를 검색합니다.")
    @GetMapping("/search/price")
    public ResponseEntity<ApiResponse<List<BookResponse>>> searchByPriceRange(
            @Parameter(description = "최소 가격", required = true) @RequestParam Integer minPrice,
            @Parameter(description = "최대 가격", required = true) @RequestParam Integer maxPrice) {
        log.info("GET /api/books/search/price - 가격 범위 검색: {} ~ {}", minPrice, maxPrice);
        List<BookResponse> response = bookService.searchByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(ApiResponse.success("가격 범위 검색 성공", response));
    }

    @Operation(summary = "재고 보유 도서 조회", description = "재고가 1권 이상인 도서만 조회합니다.")
    @GetMapping("/in-stock")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBooksInStock() {
        log.info("GET /api/books/in-stock - 재고 있는 도서 조회");
        List<BookResponse> response = bookService.getBooksInStock();
        return ResponseEntity.ok(ApiResponse.success("재고 있는 도서 조회 성공", response));
    }

    @Operation(summary = "도서 정보 수정", description = "도서의 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @Parameter(description = "수정할 도서 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request) {
        log.info("PUT /api/books/{} - 도서 수정 요청", id);
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success("도서가 성공적으로 수정되었습니다.", response));
    }

    @Operation(summary = "도서 삭제", description = "도서를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(
            @Parameter(description = "삭제할 도서 ID", required = true) @PathVariable Long id) {
        log.info("DELETE /api/books/{} - 도서 삭제 요청", id);
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("도서가 성공적으로 삭제되었습니다."));
    }

    @Operation(summary = "재고 증가", description = "도서의 재고를 지정한 수량만큼 증가시킵니다.")
    @PostMapping("/{id}/stock/increase")
    public ResponseEntity<ApiResponse<BookResponse>> increaseStock(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long id,
            @Parameter(description = "증가시킬 수량", required = true) @RequestParam int quantity) {
        log.info("POST /api/books/{}/stock/increase - 재고 증가 요청: {}", id, quantity);
        BookResponse response = bookService.increaseStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("재고가 증가되었습니다.", response));
    }

    @Operation(summary = "재고 감소", description = "도서의 재고를 지정한 수량만큼 감소시킵니다.")
    @PostMapping("/{id}/stock/decrease")
    public ResponseEntity<ApiResponse<BookResponse>> decreaseStock(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long id,
            @Parameter(description = "감소시킬 수량", required = true) @RequestParam int quantity) {
        log.info("POST /api/books/{}/stock/decrease - 재고 감소 요청: {}", id, quantity);
        BookResponse response = bookService.decreaseStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("재고가 감소되었습니다.", response));
    }
}
package dev.ime.controller;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.ime.common.GlobalConstants;
import dev.ime.dto.BookDto;
import dev.ime.dto.PaginationDto;
import dev.ime.model.PaginatedResult;
import dev.ime.port.ControllerPort;
import dev.ime.port.ServicePort;
import dev.ime.utils.PaginationUtils;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/books")
public class BookController implements ControllerPort<BookDto> {

	private static final Logger logger = LoggerFactory.getLogger(BookController.class);
	private final ServicePort<BookDto> bookService;
    private final PaginationUtils paginationUtils;
    
	public BookController(ServicePort<BookDto> bookService, PaginationUtils paginationUtils) {
		super();
		this.bookService = bookService;
		this.paginationUtils = paginationUtils;
	}

	@Override
	@GetMapping
	public ResponseEntity<PaginatedResult<BookDto>> getAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = GlobalConstants.BOOK_ID) String sortBy,
			@RequestParam(defaultValue = GlobalConstants.DIR_A) String sortDir) {
				
        logger.info("GET /books (paginated) - Retrieve with pagination");
		Set<String> attrsSet = Set.of(GlobalConstants.BOOK_ID, GlobalConstants.BOOK_ISBN, GlobalConstants.BOOK_TITLE);

        PaginationDto paginationDto = paginationUtils.createPaginationDto(page, size, sortBy, sortDir, attrsSet, GlobalConstants.BOOK_ID);
        PageRequest pageRequest = paginationUtils.createPageRequest(paginationDto);
        Page<BookDto> pageDtoList = bookService.getAll(pageRequest);
        PaginatedResult<BookDto> pageResult = paginationUtils.createPaginatedResponse(pageDtoList);
        
        return ResponseEntity.ok(pageResult);
	}

	@Override
    @GetMapping("/{id}")
	public ResponseEntity<BookDto> getById(@PathVariable Long id) {
		logger.info("GET /books/{} - Retrieve by ID", id);
        Optional<BookDto> dtoOpt = bookService.getById(id);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
    @PostMapping
	public ResponseEntity<BookDto> create(@Valid @RequestBody BookDto item) {
		 logger.info("POST /books - Create new one");
	        Optional<BookDto> savedDtoOpt = bookService.create(item);
	        if (savedDtoOpt.isPresent()) {
	        	BookDto savedDto = savedDtoOpt.get();
	            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
	                    .path("/{id}")
	                    .buildAndExpand(savedDto.publisherId())
	                    .toUri();
	            return ResponseEntity.created(location).body(savedDto);
	        } else {
	            return ResponseEntity.badRequest().build();
	        }
	}

	@Override
    @PutMapping("/{id}")
	public ResponseEntity<BookDto> update(@PathVariable Long id, @Valid @RequestBody BookDto item) {
        logger.info("PUT /books/{} - Update", id);
        Optional<BookDto> updatedDtoOpt = bookService.update(id, item);
        return updatedDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
    @DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Long id) {
		logger.info("DELETE /books/{} - Delete", id);
        boolean deleted = bookService.deleteById(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
	}
}

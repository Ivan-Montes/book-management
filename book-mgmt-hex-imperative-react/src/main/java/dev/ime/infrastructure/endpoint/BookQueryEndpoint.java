package dev.ime.infrastructure.endpoint;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ime.application.dto.BookDto;
import dev.ime.application.dto.PaginationDto;
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.PaginatedResult;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import dev.ime.domain.port.inbound.QueryServicePort;

@RestController
@RequestMapping("/api/v1/books")
public class BookQueryEndpoint implements QueryEndpointPort<BookDto> {

	private static final Logger logger = LoggerFactory.getLogger(BookQueryEndpoint.class);
	private final QueryServicePort<BookDto> bookService;
    private final PaginationUtils paginationUtils;
    
	public BookQueryEndpoint(QueryServicePort<BookDto> bookService, PaginationUtils paginationUtils) {
		super();
		this.bookService = bookService;
		this.paginationUtils = paginationUtils;
	}

	@GetMapping
	@Override
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

	@GetMapping("/{id}")
	@Override
	public ResponseEntity<BookDto> getById(@PathVariable Long id) {
		logger.info("GET /books/{} - Retrieve by ID", id);
        Optional<BookDto> dtoOpt = bookService.getById(id);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}
}

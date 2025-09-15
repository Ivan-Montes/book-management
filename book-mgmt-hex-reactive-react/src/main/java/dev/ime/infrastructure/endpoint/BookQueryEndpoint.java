package dev.ime.infrastructure.endpoint;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ime.application.dto.BookDto;
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.PaginatedResult;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import dev.ime.domain.port.inbound.QueryServicePort;
import reactor.core.publisher.Mono;

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

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public Mono<PaginatedResult<BookDto>> getAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = GlobalConstants.BOOK_ID) String sortBy,
			@RequestParam(defaultValue = GlobalConstants.DIR_A) String sortDir) {
				
        logger.info("GET /books (paginated) - Retrieve with pagination");
		Set<String> attrsSet = Set.of(GlobalConstants.BOOK_ID, GlobalConstants.BOOK_ISBN, GlobalConstants.BOOK_TITLE);

		return Mono.just(paginationUtils.createPaginationDto(page, size, sortBy, sortDir, attrsSet, GlobalConstants.BOOK_ID))
				.map(paginationUtils::createPageRequest)
				.flatMap(bookService::getAll)
				.map(paginationUtils::createPaginatedResponse);
	}

	@GetMapping("/{id}")
	@Override
	public Mono<ResponseEntity<BookDto>> getById(@PathVariable UUID id) {
		logger.info("GET /books/{} - Retrieve by ID", id);
		return bookService.getById(id)
                .map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));	
	}
}

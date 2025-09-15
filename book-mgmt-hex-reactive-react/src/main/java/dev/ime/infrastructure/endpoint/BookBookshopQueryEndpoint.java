package dev.ime.infrastructure.endpoint;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ime.application.dto.BookBookshopDto;
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.PaginatedResult;
import dev.ime.domain.port.inbound.CompositeIdQueryEndpointPort;
import dev.ime.domain.port.inbound.CompositeIdQueryServicePort;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/bookbookshops")
public class BookBookshopQueryEndpoint implements CompositeIdQueryEndpointPort<BookBookshopDto>{

	private static final Logger logger = LoggerFactory.getLogger(BookBookshopQueryEndpoint.class);
	private final CompositeIdQueryServicePort<BookBookshopDto> bookbookshopService;
    private final PaginationUtils paginationUtils;
    
	public BookBookshopQueryEndpoint(CompositeIdQueryServicePort<BookBookshopDto> bookbookshopService,
			PaginationUtils paginationUtils) {
		super();
		this.bookbookshopService = bookbookshopService;
		this.paginationUtils = paginationUtils;
	}

	@GetMapping
	@Override
	public Mono<PaginatedResult<BookBookshopDto>> getAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = GlobalConstants.BBS_ID) String sortBy,
			@RequestParam(defaultValue = GlobalConstants.DIR_A) String sortDir) {

        logger.info("GET /bookbookshops (paginated) - Retrieve with pagination");
		Set<String> attrsSet = Set.of(GlobalConstants.BBS_ID, GlobalConstants.BBS_PRICE, GlobalConstants.BBS_UNITS);

		return Mono.just(paginationUtils.createPaginationDto(page, size, sortBy, sortDir, attrsSet, GlobalConstants.BBS_ID))
				.map(paginationUtils::createPageRequest)
				.flatMap(bookbookshopService::getAll)
				.map(paginationUtils::createPaginatedResponse);
	}

	@GetMapping("/book/{bookId}/bookshop/{bookshopId}")
	@Override
	public Mono<ResponseEntity<BookBookshopDto>> getById(@PathVariable UUID bookId, @PathVariable UUID bookshopId) {
		logger.info("GET /bookbookshops/{}:{} - Retrieve by ID", bookId, bookshopId);
		return bookbookshopService.getById(bookId, bookshopId)
                .map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));		
	}
}

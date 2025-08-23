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

import dev.ime.application.dto.BookBookshopDto;
import dev.ime.application.dto.PaginationDto;
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.PaginatedResult;
import dev.ime.domain.port.inbound.CompositeIdQueryEndpointPort;
import dev.ime.domain.port.inbound.CompositeIdQueryServicePort;

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
	public ResponseEntity<PaginatedResult<BookBookshopDto>> getAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = GlobalConstants.BBS_ID) String sortBy,
			@RequestParam(defaultValue = GlobalConstants.DIR_A) String sortDir) {

        logger.info("GET /bookbookshops (paginated) - Retrieve with pagination");
		Set<String> attrsSet = Set.of(GlobalConstants.BBS_ID, GlobalConstants.BBS_PRICE, GlobalConstants.BBS_UNITS);

        PaginationDto paginationDto = paginationUtils.createPaginationDto(page, size, sortBy, sortDir, attrsSet, GlobalConstants.BBS_ID);
        PageRequest pageRequest = paginationUtils.createPageRequest(paginationDto);
        Page<BookBookshopDto> pageDtoList = bookbookshopService.getAll(pageRequest);
        PaginatedResult<BookBookshopDto> pageResult = paginationUtils.createPaginatedResponse(pageDtoList);
        
        return ResponseEntity.ok(pageResult);
	}

	@GetMapping("/book/{bookId}/bookshop/{bookshopId}")
	@Override
	public ResponseEntity<BookBookshopDto> getById(@PathVariable Long bookId, @PathVariable Long bookshopId) {
		logger.info("GET /bookbookshops/{}:{} - Retrieve by ID", bookId, bookshopId);
        Optional<BookBookshopDto> dtoOpt = bookbookshopService.getById(bookId, bookshopId);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}
}

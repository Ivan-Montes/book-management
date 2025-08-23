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

import dev.ime.application.dto.BookshopDto;
import dev.ime.application.dto.PaginationDto;
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.PaginatedResult;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import dev.ime.domain.port.inbound.QueryServicePort;

@RestController
@RequestMapping("/api/v1/bookshops")
public class BookshopQueryEndpoint implements QueryEndpointPort<BookshopDto> {

	private static final Logger logger = LoggerFactory.getLogger(BookshopQueryEndpoint.class);
	private final QueryServicePort<BookshopDto> bookshopService;
    private final PaginationUtils paginationUtils;
    
	public BookshopQueryEndpoint(QueryServicePort<BookshopDto> bookshopService, PaginationUtils paginationUtils) {
		super();
		this.bookshopService = bookshopService;
		this.paginationUtils = paginationUtils;
	}

    @GetMapping
	@Override
	public ResponseEntity<PaginatedResult<BookshopDto>> getAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = GlobalConstants.BS_ID) String sortBy,
			@RequestParam(defaultValue = GlobalConstants.DIR_A) String sortDir) {

        logger.info("GET /bookshops (paginated) - Retrieve with pagination");
		Set<String> attrsSet = Set.of(GlobalConstants.BS_ID, GlobalConstants.BS_NAME);

        PaginationDto paginationDto = paginationUtils.createPaginationDto(page, size, sortBy, sortDir, attrsSet, GlobalConstants.BS_ID);
        PageRequest pageRequest = paginationUtils.createPageRequest(paginationDto);
        Page<BookshopDto> pageDtoList = bookshopService.getAll(pageRequest);
        PaginatedResult<BookshopDto> pageResult = paginationUtils.createPaginatedResponse(pageDtoList);
        
        return ResponseEntity.ok(pageResult);
	}

    @GetMapping("/{id}")
	@Override
	public ResponseEntity<BookshopDto> getById(@PathVariable Long id) {
		logger.info("GET /bookshops/{} - Retrieve by ID", id);
        Optional<BookshopDto> dtoOpt = bookshopService.getById(id);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}
}

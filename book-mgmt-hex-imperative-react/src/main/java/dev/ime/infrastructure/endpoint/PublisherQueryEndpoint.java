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

import dev.ime.application.dto.PaginationDto;
import dev.ime.application.dto.PublisherDto;
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.PaginatedResult;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import dev.ime.domain.port.inbound.QueryServicePort;

@RestController
@RequestMapping("/api/v1/publishers")
public class PublisherQueryEndpoint implements QueryEndpointPort<PublisherDto> {

	private static final Logger logger = LoggerFactory.getLogger(PublisherQueryEndpoint.class);
	private final QueryServicePort<PublisherDto> publisherService;
    private final PaginationUtils paginationUtils;
    
	public PublisherQueryEndpoint(QueryServicePort<PublisherDto> publisherService, PaginationUtils paginationUtils) {
		super();
		this.publisherService = publisherService;
		this.paginationUtils = paginationUtils;
	}

	@GetMapping
	@Override
	public ResponseEntity<PaginatedResult<PublisherDto>> getAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = GlobalConstants.PUBLI_ID) String sortBy,
			@RequestParam(defaultValue = GlobalConstants.DIR_A) String sortDir) {

        logger.info("GET /publishers (paginated) - Retrieve with pagination");
		Set<String> attrsSet = Set.of(GlobalConstants.PUBLI_ID, GlobalConstants.PUBLI_NAME);

        PaginationDto paginationDto = paginationUtils.createPaginationDto(page, size, sortBy, sortDir, attrsSet, GlobalConstants.PUBLI_ID);
        PageRequest pageRequest = paginationUtils.createPageRequest(paginationDto);
        Page<PublisherDto> pageDtoList = publisherService.getAll(pageRequest);
        PaginatedResult<PublisherDto> pageResult = paginationUtils.createPaginatedResponse(pageDtoList);
        
        return ResponseEntity.ok(pageResult);
	}

    @GetMapping("/{id}")
	@Override
	public ResponseEntity<PublisherDto> getById(@PathVariable Long id) {
		logger.info("GET /publishers/{} - Retrieve by ID", id);
        Optional<PublisherDto> dtoOpt = publisherService.getById(id);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}
}

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

import dev.ime.application.dto.PublisherDto;
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.PaginatedResult;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import dev.ime.domain.port.inbound.QueryServicePort;
import reactor.core.publisher.Mono;

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

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public Mono<PaginatedResult<PublisherDto>> getAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = GlobalConstants.PUBLI_ID) String sortBy,
			@RequestParam(defaultValue = GlobalConstants.DIR_A) String sortDir) {

        logger.info("GET /publishers (paginated) - Retrieve with pagination");
		Set<String> attrsSet = Set.of(GlobalConstants.PUBLI_ID, GlobalConstants.PUBLI_NAME);
        
		return Mono.just(paginationUtils.createPaginationDto(page, size, sortBy, sortDir, attrsSet, GlobalConstants.PUBLI_ID))
				.map(paginationUtils::createPageRequest)
				.flatMap(publisherService::getAll)
				.map(paginationUtils::createPaginatedResponse);
	}

    @GetMapping("/{id}")
	@Override
	public Mono<ResponseEntity<PublisherDto>> getById(@PathVariable UUID id) {
		logger.info("GET /publishers/{} - Retrieve by ID", id);
		return publisherService.getById(id)
                .map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));	
	}
}

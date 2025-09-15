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

import dev.ime.application.dto.GenreDto;
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.PaginatedResult;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import dev.ime.domain.port.inbound.QueryServicePort;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreQueryEndpoint implements QueryEndpointPort<GenreDto> {

	private static final Logger logger = LoggerFactory.getLogger(GenreQueryEndpoint.class);
	private final QueryServicePort<GenreDto> genreService;
    private final PaginationUtils paginationUtils;
    
	public GenreQueryEndpoint(QueryServicePort<GenreDto> genreService, PaginationUtils paginationUtils) {
		super();
		this.genreService = genreService;
		this.paginationUtils = paginationUtils;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public Mono<PaginatedResult<GenreDto>> getAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size, 
			@RequestParam( defaultValue = GlobalConstants.GENRE_ID)String sortBy,
			@RequestParam( defaultValue = GlobalConstants.DIR_A)String sortDir) {	

        logger.info("GET /genres (paginated) - Retrieve with pagination");
		Set<String> attrsSet = Set.of(GlobalConstants.GENRE_ID, GlobalConstants.GENRE_NAME, GlobalConstants.GENRE_DESC);

		return Mono.just(paginationUtils.createPaginationDto(page, size, sortBy, sortDir, attrsSet, GlobalConstants.GENRE_ID))
				.map(paginationUtils::createPageRequest)
				.flatMap(genreService::getAll)
				.map(paginationUtils::createPaginatedResponse);
	}

    @GetMapping("/{id}")
    @Override
	public Mono<ResponseEntity<GenreDto>> getById(@PathVariable UUID id) {
        logger.info("GET /genres/{} - Retrieve by ID", id);
        return genreService.getById(id)
                .map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}

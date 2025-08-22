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

import dev.ime.application.dto.AuthorDto;
import dev.ime.application.dto.PaginationDto;
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.PaginatedResult;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import dev.ime.domain.port.inbound.QueryServicePort;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorQueryEndpoint implements QueryEndpointPort<AuthorDto> {

	private static final Logger logger = LoggerFactory.getLogger(AuthorQueryEndpoint.class);
	private final QueryServicePort<AuthorDto> authorService;
    private final PaginationUtils paginationUtils;
    
	public AuthorQueryEndpoint(QueryServicePort<AuthorDto> authorService, PaginationUtils paginationUtils) {
		super();
		this.authorService = authorService;
		this.paginationUtils = paginationUtils;
	}

	@GetMapping
	@Override
	public ResponseEntity<PaginatedResult<AuthorDto>> getAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = GlobalConstants.AUTHOR_ID) String sortBy,
			@RequestParam(defaultValue = GlobalConstants.DIR_A) String sortDir) {

        logger.info("GET /authors (paginated) - Retrieve with pagination");
		Set<String> attrsSet = Set.of(GlobalConstants.AUTHOR_ID, GlobalConstants.AUTHOR_NAME, GlobalConstants.AUTHOR_SUR);

        PaginationDto paginationDto = paginationUtils.createPaginationDto(page, size, sortBy, sortDir, attrsSet, GlobalConstants.AUTHOR_ID);
        PageRequest pageRequest = paginationUtils.createPageRequest(paginationDto);
        Page<AuthorDto> pageDtoList = authorService.getAll(pageRequest);
        PaginatedResult<AuthorDto> pageResult = paginationUtils.createPaginatedResponse(pageDtoList);
        
        return ResponseEntity.ok(pageResult);
	}

    @GetMapping("/{id}")
	@Override
	public ResponseEntity<AuthorDto> getById(@PathVariable Long id) {
    	logger.info("GET /authors/{} - Retrieve by ID", id);
        Optional<AuthorDto> dtoOpt = authorService.getById(id);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}
}

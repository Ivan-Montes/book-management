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
import dev.ime.dto.AuthorDto;
import dev.ime.dto.PaginationDto;
import dev.ime.model.PaginatedResult;
import dev.ime.port.ControllerPort;
import dev.ime.port.ServicePort;
import dev.ime.utils.PaginationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/authors")
public class AuthorController implements ControllerPort<AuthorDto> {

	private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);
	private final ServicePort<AuthorDto> authorService;
    private final PaginationUtils paginationUtils;
    
	public AuthorController(ServicePort<AuthorDto> authorService, PaginationUtils paginationUtils) {
		super();
		this.authorService = authorService;
		this.paginationUtils = paginationUtils;
	}

	@Override
	@GetMapping
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

	@Override
    @GetMapping("/{id}")
	public ResponseEntity<AuthorDto> getById(@PathVariable Long id) {
		logger.info("GET /authors/{} - Retrieve by ID", id);
        Optional<AuthorDto> dtoOpt = authorService.getById(id);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
    @PostMapping
	public ResponseEntity<AuthorDto> create(@Valid @RequestBody AuthorDto item) {
		 logger.info("POST /authors - Create new one");
	        Optional<AuthorDto> savedDtoOpt = authorService.create(item);
	        if (savedDtoOpt.isPresent()) {
	        	AuthorDto savedDto = savedDtoOpt.get();
	            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
	                    .path("/{id}")
	                    .buildAndExpand(savedDto.authorId())
	                    .toUri();
	            return ResponseEntity.created(location).body(savedDto);
	        } else {
	            return ResponseEntity.badRequest().build();
	        }
	}

	@Override
    @PutMapping("/{id}")
	public ResponseEntity<AuthorDto> update(@PathVariable Long id, @Valid @RequestBody AuthorDto item) {
        logger.info("PUT /authors/{} - Update", id);
        Optional<AuthorDto> updatedDtoOpt = authorService.update(id, item);
        return updatedDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
    @DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Long id) {
		logger.info("DELETE /authors/{} - Delete", id);
        boolean deleted = authorService.deleteById(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
	}	
}

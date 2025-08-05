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
import dev.ime.dto.GenreDto;
import dev.ime.dto.PaginationDto;
import dev.ime.model.PaginatedResult;
import dev.ime.port.ControllerPort;
import dev.ime.port.ServicePort;
import dev.ime.utils.PaginationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/genres")
public class GenreController implements ControllerPort<GenreDto> {

	private static final Logger logger = LoggerFactory.getLogger(GenreController.class);
	private final ServicePort<GenreDto> genreService;
    private final PaginationUtils paginationUtils;

	public GenreController(ServicePort<GenreDto> genreService, PaginationUtils paginationUtils) {
		super();
		this.genreService = genreService;
		this.paginationUtils = paginationUtils;
	}

	@Override
    @GetMapping
	public ResponseEntity<PaginatedResult<GenreDto>> getAll(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size, 
			@RequestParam( defaultValue = GlobalConstants.GENRE_ID)String sortBy,
			@RequestParam( defaultValue = GlobalConstants.DIR_A)String sortDir) {	

        logger.info("GET /genres (paginated) - Retrieve with pagination");
		Set<String> attrsSet = Set.of(GlobalConstants.GENRE_ID,GlobalConstants.GENRE_NAME,GlobalConstants.GENRE_DESC);

		PaginationDto paginationDto = paginationUtils.createPaginationDto(page, size, sortBy, sortDir, attrsSet, GlobalConstants.GENRE_ID);
        PageRequest pageRequest = paginationUtils.createPageRequest(paginationDto);
        Page<GenreDto> pageDtoList = genreService.getAll(pageRequest);
        PaginatedResult<GenreDto> pageResult = paginationUtils.createPaginatedResponse(pageDtoList);

        return ResponseEntity.ok(pageResult);
	}	

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getById(@PathVariable Long id) {
        logger.info("GET /genres/{} - Retrieve by ID", id);
        Optional<GenreDto> dtoOpt = genreService.getById(id);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @PostMapping
    public ResponseEntity<GenreDto> create(@Valid @RequestBody GenreDto item) {
        logger.info("POST /genres - Create new one");
        Optional<GenreDto> savedDtoOpt = genreService.create(item);
        if (savedDtoOpt.isPresent()) {
            GenreDto savedDto = savedDtoOpt.get();
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedDto.genreId())
                    .toUri();
            return ResponseEntity.created(location).body(savedDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<GenreDto> update(@PathVariable Long id, @Valid @RequestBody GenreDto item) {
        logger.info("PUT /genres/{} - Update", id);
        Optional<GenreDto> updatedDtoOpt = genreService.update(id, item);
        return updatedDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        logger.info("DELETE /genres/{} - Delete", id);
        boolean deleted = genreService.deleteById(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

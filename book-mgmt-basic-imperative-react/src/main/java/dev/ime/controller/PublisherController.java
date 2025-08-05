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
import dev.ime.dto.PaginationDto;
import dev.ime.dto.PublisherDto;
import dev.ime.model.PaginatedResult;
import dev.ime.port.ControllerPort;
import dev.ime.port.ServicePort;
import dev.ime.utils.PaginationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/publishers")
public class PublisherController implements ControllerPort<PublisherDto> {

	private static final Logger logger = LoggerFactory.getLogger(PublisherController.class);
	private final ServicePort<PublisherDto> publisherService;
    private final PaginationUtils paginationUtils;

	public PublisherController(ServicePort<PublisherDto> publisherService, PaginationUtils paginationUtils) {
		super();
		this.publisherService = publisherService;
		this.paginationUtils = paginationUtils;
	}

	@Override
	@GetMapping
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
	
	@Override
    @GetMapping("/{id}")
	public ResponseEntity<PublisherDto> getById(@PathVariable Long id) {
		logger.info("GET /publishers/{} - Retrieve by ID", id);
        Optional<PublisherDto> dtoOpt = publisherService.getById(id);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
    @PostMapping
	public ResponseEntity<PublisherDto> create(@Valid @RequestBody PublisherDto item) {
		 logger.info("POST /publishers - Create new one");
	        Optional<PublisherDto> savedDtoOpt = publisherService.create(item);
	        if (savedDtoOpt.isPresent()) {
	        	PublisherDto savedDto = savedDtoOpt.get();
	            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
	                    .path("/{id}")
	                    .buildAndExpand(savedDto.publisherId())
	                    .toUri();
	            return ResponseEntity.created(location).body(savedDto);
	        } else {
	            return ResponseEntity.badRequest().build();
	        }
	}

	@Override
    @PutMapping("/{id}")
	public ResponseEntity<PublisherDto> update(@PathVariable Long id, @Valid @RequestBody PublisherDto item) {
        logger.info("PUT /publishers/{} - Update", id);
        Optional<PublisherDto> updatedDtoOpt = publisherService.update(id, item);
        return updatedDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
    @DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Long id) {
		logger.info("DELETE /publishers/{} - Delete", id);
        boolean deleted = publisherService.deleteById(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
	}
}

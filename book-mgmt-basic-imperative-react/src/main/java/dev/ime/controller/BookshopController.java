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
import dev.ime.dto.BookshopDto;
import dev.ime.dto.PaginationDto;
import dev.ime.model.PaginatedResult;
import dev.ime.port.ControllerPort;
import dev.ime.port.ServicePort;
import dev.ime.utils.PaginationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bookshops")
public class BookshopController implements ControllerPort<BookshopDto> {

	private static final Logger logger = LoggerFactory.getLogger(BookshopController.class);
	private final ServicePort<BookshopDto> bookshopService;
    private final PaginationUtils paginationUtils;
    
	public BookshopController(ServicePort<BookshopDto> bookshopService, PaginationUtils paginationUtils) {
		super();
		this.bookshopService = bookshopService;
		this.paginationUtils = paginationUtils;
	}

	@Override
    @GetMapping
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

	@Override
    @GetMapping("/{id}")
	public ResponseEntity<BookshopDto> getById(@PathVariable Long id) {
		logger.info("GET /bookshops/{} - Retrieve by ID", id);
        Optional<BookshopDto> dtoOpt = bookshopService.getById(id);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
    @PostMapping
	public ResponseEntity<BookshopDto> create(@Valid @RequestBody BookshopDto item) {
		 logger.info("POST /bookshops - Create new one");
	        Optional<BookshopDto> savedDtoOpt = bookshopService.create(item);
	        if (savedDtoOpt.isPresent()) {
	        	BookshopDto savedDto = savedDtoOpt.get();
	            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
	                    .path("/{id}")
	                    .buildAndExpand(savedDto.bookshopId())
	                    .toUri();
	            return ResponseEntity.created(location).body(savedDto);
	        } else {
	            return ResponseEntity.badRequest().build();
	        }
	}

	@Override
    @PutMapping("/{id}")
	public ResponseEntity<BookshopDto> update(@PathVariable Long id, @Valid @RequestBody BookshopDto item) {
        logger.info("PUT /bookshops/{} - Update", id);
        Optional<BookshopDto> updatedDtoOpt = bookshopService.update(id, item);
        return updatedDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Override
    @DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Long id) {
		logger.info("DELETE /bookshops/{} - Delete", id);
        boolean deleted = bookshopService.deleteById(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
	}
}

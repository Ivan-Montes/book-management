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
import dev.ime.dto.BookBookshopDto;
import dev.ime.dto.PaginationDto;
import dev.ime.model.PaginatedResult;
import dev.ime.port.CompositeIdControllerPort;
import dev.ime.port.CompositeIdServicePort;
import dev.ime.utils.PaginationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bookbookshops")
public class BookBookshopController implements CompositeIdControllerPort<BookBookshopDto>{

	private static final Logger logger = LoggerFactory.getLogger(BookBookshopController.class);
	private final CompositeIdServicePort<BookBookshopDto> bookbookshopService;
    private final PaginationUtils paginationUtils;
    
	public BookBookshopController(CompositeIdServicePort<BookBookshopDto> bookbookshopService,
			PaginationUtils paginationUtils) {
		super();
		this.bookbookshopService = bookbookshopService;
		this.paginationUtils = paginationUtils;
	}
	
	@Override
	@GetMapping
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
	
	@Override
    @GetMapping("/book/{id01}/bookshop/{id02}")
	public ResponseEntity<BookBookshopDto> getById(@PathVariable Long id01, @PathVariable Long id02) {
		logger.info("GET /bookbookshops/{}:{} - Retrieve by ID", id01, id02);
        Optional<BookBookshopDto> dtoOpt = bookbookshopService.getById(id01, id02);
        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@Override
    @PostMapping
	public ResponseEntity<BookBookshopDto> create(@Valid @RequestBody BookBookshopDto item) {
		 logger.info("POST /bookbookshops - Create new one");
	        Optional<BookBookshopDto> savedDtoOpt = bookbookshopService.create(item);
	        if (savedDtoOpt.isPresent()) {
	        	BookBookshopDto savedDto = savedDtoOpt.get();
	            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
	                    .path("/book{id01}/bookshop/{id02}")
	                    .buildAndExpand(savedDto.bookId(), savedDto.bookshopId())
	                    .toUri();
	            return ResponseEntity.created(location).body(savedDto);
	        } else {
	            return ResponseEntity.badRequest().build();
	        }
	}
	
	@Override
    @PutMapping("/book/{id01}/bookshop/{id02}")
	public ResponseEntity<BookBookshopDto> update(@PathVariable Long id01, @PathVariable Long id02, @RequestBody BookBookshopDto item) {
        logger.info("PUT /bookbookshops/{}:{} - Update", id01, id02);
        Optional<BookBookshopDto> updatedDtoOpt = bookbookshopService.update(id01, id02, item);
        return updatedDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@Override
	@DeleteMapping("/book/{id01}/bookshop/{id02}")
	public ResponseEntity<Void> deleteById(@PathVariable Long id01, @PathVariable Long id02) {
		logger.info("DELETE /bookbookshops/{}:{} - Delete", id01, id02);
        boolean deleted = bookbookshopService.deleteById(id01, id02);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
	}
}

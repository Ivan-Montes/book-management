package dev.ime.infrastructure.endpoint;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ime.application.dto.BookBookshopDto;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CompositeIdCommandEndpointPort;
import dev.ime.domain.port.inbound.CompositeIdCommandServicePort;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookbookshops")
public class BookBookshopCommandEndpoint implements CompositeIdCommandEndpointPort<BookBookshopDto> {

	private static final Logger logger = LoggerFactory.getLogger(BookBookshopCommandEndpoint.class);
	private final CompositeIdCommandServicePort<BookBookshopDto> bookbookshopService;

	public BookBookshopCommandEndpoint(CompositeIdCommandServicePort<BookBookshopDto> bookbookshopService) {
		super();
		this.bookbookshopService = bookbookshopService;
	}

	@PostMapping
	@Override
	public ResponseEntity<Event> create(@Valid @RequestBody BookBookshopDto item) {
		logger.info("POST /bookbookshops - Create new one");
		Optional<Event> optEvent = bookbookshopService.create(item);
		return createResponse(optEvent, HttpStatus.CREATED);
	}

	@PutMapping("/book/{id01}/bookshop/{id02}")
	@Override
	public ResponseEntity<Event> update(@PathVariable Long id01, @PathVariable Long id02,
			@Valid @RequestBody BookBookshopDto item) {
		logger.info("PUT /bookbookshops/{}:{} - Update", id01, id02);
		Optional<Event> optEvent = bookbookshopService.update(id01, id02, item);
		return createResponse(optEvent, HttpStatus.OK);
	}

	@DeleteMapping("/book/{bookId}/bookshop/{bookshopId}")
	@Override
	public ResponseEntity<Event> deleteById(@PathVariable Long bookId, @PathVariable Long bookshopId) {
		logger.info("DELETE /bookbookshops/{}:{} - Delete", bookId, bookshopId);
		Optional<Event> optEvent = bookbookshopService.deleteById(bookId, bookshopId);
		return createResponse(optEvent, HttpStatus.OK);
	}

	private ResponseEntity<Event> createResponse(Optional<Event> optEvent, HttpStatus successStatus) {

		return optEvent.isPresent() ? new ResponseEntity<>(optEvent.get(), successStatus)
				: ResponseEntity.badRequest().build();
	}
}

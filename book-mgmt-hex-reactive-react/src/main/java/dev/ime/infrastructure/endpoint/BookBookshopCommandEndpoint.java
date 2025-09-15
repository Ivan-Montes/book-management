package dev.ime.infrastructure.endpoint;

import java.util.UUID;

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
import reactor.core.publisher.Mono;

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
	public Mono<ResponseEntity<Event>> create(@Valid @RequestBody BookBookshopDto item) {
		logger.info("POST /bookbookshops - Create new one");
		return bookbookshopService.create(item)
				.map( data -> new  ResponseEntity<>(data, HttpStatus.CREATED))
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}

	@PutMapping("/book/{id01}/bookshop/{id02}")
	@Override
	public Mono<ResponseEntity<Event>> update(@PathVariable UUID id01, @PathVariable UUID id02,
			@Valid @RequestBody BookBookshopDto item) {
		logger.info("PUT /bookbookshops/{}:{} - Update", id01, id02);
		return bookbookshopService.update(id01, id02, item)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}

	@DeleteMapping("/book/{bookId}/bookshop/{bookshopId}")
	@Override
	public Mono<ResponseEntity<Event>> deleteById(@PathVariable UUID bookId, @PathVariable UUID bookshopId) {
		logger.info("DELETE /bookbookshops/{}:{} - Delete", bookId, bookshopId);
		return bookbookshopService.deleteById(bookId, bookshopId)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}
}

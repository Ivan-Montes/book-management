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

import dev.ime.application.dto.BookDto;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/books")
public class BookCommandEndpoint implements CommandEndpointPort<BookDto> {

	private static final Logger logger = LoggerFactory.getLogger(BookCommandEndpoint.class);
	private final CommandServicePort<BookDto> bookService;

	public BookCommandEndpoint(CommandServicePort<BookDto> bookService) {
		super();
		this.bookService = bookService;
	}

	@PostMapping
	@Override
	public Mono<ResponseEntity<Event>> create(@Valid @RequestBody BookDto item) {
		logger.info("POST /books - Create new one");
		return bookService.create(item)
				.map( data -> new  ResponseEntity<>(data, HttpStatus.CREATED))
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}

	@PutMapping("/{id}")
	@Override
	public Mono<ResponseEntity<Event>> update(@PathVariable UUID id, @Valid @RequestBody BookDto item) {
		logger.info("PUT /books/{} - Update", id);
		return bookService.update(id, item)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}

	@DeleteMapping("/{id}")
	@Override
	public Mono<ResponseEntity<Event>> deleteById(@PathVariable UUID id) {
		logger.info("DELETE /books/{} - Delete", id);	
		return bookService.deleteById(id)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}
}

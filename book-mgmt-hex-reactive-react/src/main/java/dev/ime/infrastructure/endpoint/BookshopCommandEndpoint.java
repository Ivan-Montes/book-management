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

import dev.ime.application.dto.BookshopDto;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/bookshops")
public class BookshopCommandEndpoint implements CommandEndpointPort<BookshopDto> {

	private static final Logger logger = LoggerFactory.getLogger(BookshopCommandEndpoint.class);
	private final CommandServicePort<BookshopDto> bookshopService;

	public BookshopCommandEndpoint(CommandServicePort<BookshopDto> bookshopService) {
		super();
		this.bookshopService = bookshopService;
	}

	@PostMapping
	@Override
	public Mono<ResponseEntity<Event>> create(@Valid @RequestBody BookshopDto item) {
		logger.info("POST /bookshops - Create new one");
		return bookshopService.create(item)
				.map( data -> new  ResponseEntity<>(data, HttpStatus.CREATED))
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}

	@PutMapping("/{id}")
	@Override
	public Mono<ResponseEntity<Event>> update(@PathVariable UUID id, @Valid @RequestBody BookshopDto item) {
		logger.info("PUT /bookshops/{} - Update", id);
		return bookshopService.update(id, item)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}

	@DeleteMapping("/{id}")
	@Override
	public Mono<ResponseEntity<Event>> deleteById(@PathVariable UUID id) {
		logger.info("DELETE /bookshops/{} - Delete", id);	
		return bookshopService.deleteById(id)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}
}

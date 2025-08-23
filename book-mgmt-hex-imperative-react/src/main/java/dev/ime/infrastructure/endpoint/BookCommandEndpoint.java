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

import dev.ime.application.dto.BookDto;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import jakarta.validation.Valid;

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
	public ResponseEntity<Event> create(@Valid @RequestBody BookDto item) {
		logger.info("POST /books - Create new one");
		Optional<Event> optEvent = bookService.create(item);
		return createResponse(optEvent, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@Override
	public ResponseEntity<Event> update(@PathVariable Long id, @Valid @RequestBody BookDto item) {
		logger.info("PUT /books/{} - Update", id);
		Optional<Event> optEvent = bookService.update(id, item);
		return createResponse(optEvent, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Override
	public ResponseEntity<Event> deleteById(@PathVariable Long id) {
		logger.info("DELETE /books/{} - Delete", id);
		Optional<Event> optEvent = bookService.deleteById(id);
		return createResponse(optEvent, HttpStatus.OK);
	}

	private ResponseEntity<Event> createResponse(Optional<Event> optEvent, HttpStatus successStatus) {

		return optEvent.isPresent() ? new ResponseEntity<>(optEvent.get(), successStatus)
				: ResponseEntity.badRequest().build();
	}
}

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

import dev.ime.application.dto.BookshopDto;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import jakarta.validation.Valid;

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
	public ResponseEntity<Event> create(@Valid @RequestBody BookshopDto item) {
		logger.info("POST /bookshops - Create new one");
		Optional<Event> optEvent = bookshopService.create(item);
		return createResponse(optEvent, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@Override
	public ResponseEntity<Event> update(@PathVariable Long id, @Valid @RequestBody BookshopDto item) {
		logger.info("PUT /bookshops/{} - Update", id);
		Optional<Event> optEvent = bookshopService.update(id, item);
		return createResponse(optEvent, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Override
	public ResponseEntity<Event> deleteById(@PathVariable Long id) {
		logger.info("DELETE /bookshops/{} - Delete", id);
		Optional<Event> optEvent = bookshopService.deleteById(id);
		return createResponse(optEvent, HttpStatus.OK);
	}

	private ResponseEntity<Event> createResponse(Optional<Event> optEvent, HttpStatus successStatus) {

		return optEvent.isPresent() ? new ResponseEntity<>(optEvent.get(), successStatus)
				: ResponseEntity.badRequest().build();
	}
}

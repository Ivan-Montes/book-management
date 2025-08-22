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

import dev.ime.application.dto.AuthorDto;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorCommandEndpoint implements CommandEndpointPort<AuthorDto> {

	private static final Logger logger = LoggerFactory.getLogger(AuthorCommandEndpoint.class);
	private final CommandServicePort<AuthorDto> authorService;

	public AuthorCommandEndpoint(CommandServicePort<AuthorDto> authorService) {
		super();
		this.authorService = authorService;
	}

	@PostMapping
	@Override
	public ResponseEntity<Event> create(@Valid @RequestBody AuthorDto item) {
		logger.info("POST /authors - Create new one");
		Optional<Event> optEvent = authorService.create(item);
		return createResponse(optEvent, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@Override
	public ResponseEntity<Event> update(@PathVariable Long id, @Valid @RequestBody AuthorDto item) {
		logger.info("PUT /authors/{} - Update", id);
		Optional<Event> optEvent = authorService.update(id, item);
		return createResponse(optEvent, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Override
	public ResponseEntity<Event> deleteById(@PathVariable Long id) {
		logger.info("DELETE /authors/{} - Delete", id);
		Optional<Event> optEvent = authorService.deleteById(id);
		return createResponse(optEvent, HttpStatus.OK);
	}

	private ResponseEntity<Event> createResponse(Optional<Event> optEvent, HttpStatus successStatus) {

		return optEvent.isPresent() ? new ResponseEntity<>(optEvent.get(), successStatus)
				: ResponseEntity.badRequest().build();
	}
}

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

import dev.ime.application.dto.GenreDto;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreCommandEndpoint implements CommandEndpointPort<GenreDto> {

	private static final Logger logger = LoggerFactory.getLogger(GenreCommandEndpoint.class);
	private final CommandServicePort<GenreDto> genreService;

	public GenreCommandEndpoint(CommandServicePort<GenreDto> genreService) {
		super();
		this.genreService = genreService;
	}

	@PostMapping
	@Override
	public ResponseEntity<Event> create(@Valid @RequestBody GenreDto item) {
		logger.info("POST /genres - Create new one");
		Optional<Event> optEvent = genreService.create(item);
		return createResponse(optEvent, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@Override
	public ResponseEntity<Event> update(@PathVariable Long id, @Valid @RequestBody GenreDto item) {
		logger.info("PUT /genres/{} - Update", id);
		Optional<Event> optEvent = genreService.update(id, item);
		return createResponse(optEvent, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Override
	public ResponseEntity<Event> deleteById(@PathVariable Long id) {
		logger.info("DELETE /genres/{} - Delete", id);
		Optional<Event> optEvent = genreService.deleteById(id);
		return createResponse(optEvent, HttpStatus.OK);
	}

	private ResponseEntity<Event> createResponse(Optional<Event> optEvent, HttpStatus successStatus) {

		return optEvent.isPresent() ? new ResponseEntity<>(optEvent.get(), successStatus)
				: ResponseEntity.badRequest().build();
	}
}

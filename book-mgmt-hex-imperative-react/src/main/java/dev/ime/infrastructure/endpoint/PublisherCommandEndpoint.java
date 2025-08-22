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

import dev.ime.application.dto.PublisherDto;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/publishers")
public class PublisherCommandEndpoint implements CommandEndpointPort<PublisherDto> {

	private static final Logger logger = LoggerFactory.getLogger(PublisherCommandEndpoint.class);
	private final CommandServicePort<PublisherDto> publisherService;

	public PublisherCommandEndpoint(CommandServicePort<PublisherDto> publisherService) {
		super();
		this.publisherService = publisherService;
	}

	@PostMapping
	@Override
	public ResponseEntity<Event> create(@Valid @RequestBody PublisherDto item) {
		logger.info("POST /publishers - Create new one");
		Optional<Event> optEvent = publisherService.create(item);
		return createResponse(optEvent, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@Override
	public ResponseEntity<Event> update(@PathVariable Long id, @Valid @RequestBody PublisherDto item) {
		logger.info("PUT /publishers/{} - Update", id);
		Optional<Event> optEvent = publisherService.update(id, item);
		return createResponse(optEvent, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Override
	public ResponseEntity<Event> deleteById(@PathVariable Long id) {
		logger.info("DELETE /publishers/{} - Delete", id);
		Optional<Event> optEvent = publisherService.deleteById(id);
		return createResponse(optEvent, HttpStatus.OK);
	}

	private ResponseEntity<Event> createResponse(Optional<Event> optEvent, HttpStatus successStatus) {

		return optEvent.isPresent() ? new ResponseEntity<>(optEvent.get(), successStatus)
				: ResponseEntity.badRequest().build();
	}
}

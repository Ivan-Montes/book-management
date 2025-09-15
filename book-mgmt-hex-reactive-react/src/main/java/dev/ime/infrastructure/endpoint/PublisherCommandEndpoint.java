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

import dev.ime.application.dto.PublisherDto;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

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
	public Mono<ResponseEntity<Event>> create(@Valid @RequestBody PublisherDto item) {
		logger.info("POST /publishers - Create new one");
		return publisherService.create(item)
				.map( data -> new  ResponseEntity<>(data, HttpStatus.CREATED))
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}

	@PutMapping("/{id}")
	@Override
	public Mono<ResponseEntity<Event>> update(@PathVariable UUID id, @Valid @RequestBody PublisherDto item) {
		logger.info("PUT /publishers/{} - Update", id);
		return publisherService.update(id, item)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}

	@DeleteMapping("/{id}")
	@Override
	public Mono<ResponseEntity<Event>> deleteById(@PathVariable UUID id) {
		logger.info("DELETE /publishers/{} - Delete", id);	
		return publisherService.deleteById(id)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}
}

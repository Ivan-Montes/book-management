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

import dev.ime.application.dto.AuthorDto;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

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
	public Mono<ResponseEntity<Event>> create(@Valid @RequestBody AuthorDto item) {
		logger.info("POST /authors - Create new one");
		return authorService.create(item)
				.map( data -> new  ResponseEntity<>(data, HttpStatus.CREATED))
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}

	@PutMapping("/{id}")
	@Override
	public Mono<ResponseEntity<Event>> update(@PathVariable UUID id, @Valid @RequestBody AuthorDto item) {
		logger.info("PUT /authors/{} - Update", id);
		return authorService.update(id, item)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}

	@DeleteMapping("/{id}")
	@Override
	public Mono<ResponseEntity<Event>> deleteById(@PathVariable UUID id) {
		logger.info("DELETE /authors/{} - Delete", id);		
		return authorService.deleteById(id)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
	}
}

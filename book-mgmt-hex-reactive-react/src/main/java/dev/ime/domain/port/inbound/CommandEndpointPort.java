package dev.ime.domain.port.inbound;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import dev.ime.domain.model.Event;
import reactor.core.publisher.Mono;

public interface CommandEndpointPort<T> {

	Mono<ResponseEntity<Event>> create(T item);
	Mono<ResponseEntity<Event>> update(UUID id, T item);
	Mono<ResponseEntity<Event>> deleteById(UUID id);
}

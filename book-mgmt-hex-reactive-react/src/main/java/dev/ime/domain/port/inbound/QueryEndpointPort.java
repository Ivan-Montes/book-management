package dev.ime.domain.port.inbound;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import dev.ime.domain.model.PaginatedResult;
import reactor.core.publisher.Mono;

public interface QueryEndpointPort<T> {

	Mono<PaginatedResult<T>> getAll(Integer page, Integer size, String sortBy, String sortDir);
	Mono<ResponseEntity<T>> getById(UUID id);
}

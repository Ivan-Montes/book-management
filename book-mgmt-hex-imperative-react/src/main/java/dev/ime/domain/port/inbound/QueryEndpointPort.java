package dev.ime.domain.port.inbound;

import org.springframework.http.ResponseEntity;

import dev.ime.domain.model.PaginatedResult;

public interface QueryEndpointPort<T> {

	ResponseEntity<PaginatedResult<T>> getAll(Integer page, Integer size, String sortBy, String sortDir);
	ResponseEntity<T> getById(Long id);
}

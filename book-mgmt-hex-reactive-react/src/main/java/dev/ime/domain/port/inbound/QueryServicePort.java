package dev.ime.domain.port.inbound;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Mono;

public interface QueryServicePort<T> {

	Mono<Page<T>> getAll(Pageable pageable);
	Mono<T> getById(UUID id);
}

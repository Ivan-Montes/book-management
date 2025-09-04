package dev.ime.domain.port.outbound;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Mono;

public interface CompositeIdReadRepositoryPort<T> {

	Mono<Page<T>> findAll(Pageable pageable);
	Mono<T> findById(UUID id01, UUID id02);
}

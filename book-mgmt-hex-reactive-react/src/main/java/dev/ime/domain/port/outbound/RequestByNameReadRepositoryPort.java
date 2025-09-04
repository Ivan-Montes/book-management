package dev.ime.domain.port.outbound;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface RequestByNameReadRepositoryPort {

	Mono<Boolean> existsByName(String name);
	Mono<Boolean> findByNameAndIdNot(String name, UUID id);
}

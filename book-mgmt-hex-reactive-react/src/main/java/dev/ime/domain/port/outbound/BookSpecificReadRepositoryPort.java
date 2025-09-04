package dev.ime.domain.port.outbound;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface BookSpecificReadRepositoryPort {
	
	Mono<Boolean> existsByIsbn(String isbn);	
	Mono<Boolean> findByIsbnAndIdNot(String isbn, UUID id);
}

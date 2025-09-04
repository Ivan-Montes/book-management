package dev.ime.domain.port.outbound;

import dev.ime.domain.model.Event;
import reactor.core.publisher.Mono;

public interface ProjectorPort {
	
	Mono<Void> create(Event event);
	Mono<Void> update(Event event);
	Mono<Void> deleteById(Event event);
}

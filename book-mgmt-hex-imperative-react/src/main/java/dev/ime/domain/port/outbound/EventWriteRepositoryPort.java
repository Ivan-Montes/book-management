package dev.ime.domain.port.outbound;

import java.util.Optional;

import dev.ime.domain.model.Event;

public interface EventWriteRepositoryPort {
	
	Optional<Event> save(Event event);
}

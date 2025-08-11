package dev.ime.domain.port.inbound;

import java.util.Optional;

import dev.ime.domain.model.Event;

public interface CommandServicePort<T>  {

	Optional<Event> create(T item);
	Optional<Event> update(Long id, T item);
	Optional<Event> deleteById(Long id);
}

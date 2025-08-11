package dev.ime.domain.port.inbound;

import java.util.Optional;

import dev.ime.domain.model.Event;

public interface CompositeIdCommandServicePort<T> {

	Optional<Event> create(T item);
	Optional<Event> update(Long id01, Long id02, T item);
	Optional<Event> deleteById(Long id01, Long id02);
}

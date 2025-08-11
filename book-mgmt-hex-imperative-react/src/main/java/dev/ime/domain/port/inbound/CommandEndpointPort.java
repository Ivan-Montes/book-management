package dev.ime.domain.port.inbound;

import org.springframework.http.ResponseEntity;

import dev.ime.domain.model.Event;

public interface CommandEndpointPort<T> {

    ResponseEntity<Event> create(T item);
    ResponseEntity<Event> update(Long id, T item);
    ResponseEntity<Event> deleteById(Long id);
}

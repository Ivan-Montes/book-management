package dev.ime.domain.port.inbound;

import org.springframework.http.ResponseEntity;

import dev.ime.domain.model.Event;

public interface CompositeIdCommandEndpointPort<T> {

    ResponseEntity<Event> create(T item);
    ResponseEntity<Event> update(Long id01, Long id02, T item);
    ResponseEntity<Event> deleteById(Long id01, Long id02);
}

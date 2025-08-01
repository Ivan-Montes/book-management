package dev.ime.port;

import org.springframework.http.ResponseEntity;

import dev.ime.model.PaginatedResult;

public interface ControllerPort<T> {

    ResponseEntity<PaginatedResult<T>> getAll(Integer page,Integer size,String sortBy,String sortDir);
    ResponseEntity<T> getById(Long id);
    ResponseEntity<T> create(T item);
    ResponseEntity<T> update(Long id, T item);
    ResponseEntity<Void> deleteById(Long id);
}

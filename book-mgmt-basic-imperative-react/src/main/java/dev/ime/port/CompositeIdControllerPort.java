package dev.ime.port;

import org.springframework.http.ResponseEntity;

import dev.ime.model.PaginatedResult;

public interface CompositeIdControllerPort<T> {
    
	ResponseEntity<PaginatedResult<T>> getAll(Integer page,Integer size,String sortBy,String sortDir);
    ResponseEntity<T> getById(Long id01, Long id02);
    ResponseEntity<T> create(T item);
    ResponseEntity<T> update(Long id01, Long id02, T item);
    ResponseEntity<Void> deleteById(Long id01, Long id02);
}

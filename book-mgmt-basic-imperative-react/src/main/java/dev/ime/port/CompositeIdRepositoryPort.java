package dev.ime.port;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompositeIdRepositoryPort<T> {

	Page<T> findAll(Pageable pageable);
	Optional<T> findById(Long id01, Long id02);		
	Optional<T> save(T item);	
	Optional<T> update(T item);
	boolean deleteById(Long id01, Long id02);
}

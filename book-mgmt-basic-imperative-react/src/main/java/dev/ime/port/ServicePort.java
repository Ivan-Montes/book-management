package dev.ime.port;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicePort<T> {

	Page<T> getAll(Pageable pageable);
	Optional<T> getById(Long id);
	Optional<T> create(T item);
	Optional<T> update(Long id, T item);
	boolean deleteById(Long id);
}

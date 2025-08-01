package dev.ime.port;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompositeIdServicePort<T> {

	Page<T> getAll(Pageable pageable);
	Optional<T> getById(Long id01, Long id02);
	Optional<T> create(T item);
	Optional<T> update(Long id01, Long id02, T item);
	boolean deleteById(Long id01, Long id02);
}

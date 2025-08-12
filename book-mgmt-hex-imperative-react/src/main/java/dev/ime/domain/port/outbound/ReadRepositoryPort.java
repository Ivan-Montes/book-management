package dev.ime.domain.port.outbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReadRepositoryPort<T> {

	Page<T> findAll(Pageable pageable);
	Optional<T> findById(Long id);		
}

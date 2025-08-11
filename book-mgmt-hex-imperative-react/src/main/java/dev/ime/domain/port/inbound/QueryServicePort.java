package dev.ime.domain.port.inbound;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryServicePort<T> {

	Page<T> getAll(Pageable pageable);
	Optional<T> getById(Long id);
}

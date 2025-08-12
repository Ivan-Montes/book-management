package dev.ime.domain.port.outbound;

public interface RequestByNameReadRepositoryPort {

	boolean existsByName(String name);
	boolean findByNameAndIdNot(String name, Long id);
}

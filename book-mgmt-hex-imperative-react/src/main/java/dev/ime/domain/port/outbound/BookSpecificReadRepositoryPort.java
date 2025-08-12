package dev.ime.domain.port.outbound;

public interface BookSpecificReadRepositoryPort {
	
	boolean existsByIsbn(String isbn);	
	boolean findByIsbnAndIdNot(String isbn, Long id);
}

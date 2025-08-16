package dev.ime.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.ime.infrastructure.entity.AuthorJpaEntity;

public interface AuthorRepository extends JpaRepository<AuthorJpaEntity, Long> {

	@Query(value = "SELECT DISTINCT a FROM AuthorJpaEntity a " + "LEFT JOIN FETCH a.books b "
			+ "LEFT JOIN FETCH b.authors " + "LEFT JOIN FETCH b.genre "
			+ "LEFT JOIN FETCH b.publisher", countQuery = "SELECT COUNT(DISTINCT a) FROM AuthorJpaEntity a")
	Page<AuthorJpaEntity> findAllWithBooksAuthorsGenresPublishers(Pageable pageable);

	@Query("SELECT DISTINCT a FROM AuthorJpaEntity a " + "LEFT JOIN FETCH a.books b " + "LEFT JOIN FETCH b.authors "
			+ "LEFT JOIN FETCH b.genre " + "LEFT JOIN FETCH b.publisher " + "WHERE a.authorId = :id")
	Optional<AuthorJpaEntity> findByIdWithBooksAndAllRelations(Long id);
}

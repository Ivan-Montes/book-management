package dev.ime.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.ime.infrastructure.entity.GenreJpaEntity;

public interface GenreRepository extends JpaRepository<GenreJpaEntity, Long>{
	
	@EntityGraph(attributePaths = {"books", "books.publisher"})
	List<GenreJpaEntity>findAll();

	@EntityGraph(attributePaths = {"books", "books.publisher"})
	Page<GenreJpaEntity>findAll(Pageable pageable);
	
	@EntityGraph(attributePaths = {"books", "books.publisher"})
	Optional<GenreJpaEntity> findById(Long id);
	
	boolean existsByName(String name);
	
	List<GenreJpaEntity> findByNameAndGenreIdNot(String name, Long id);
}

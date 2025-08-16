package dev.ime.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.ime.infrastructure.entity.BookshopJpaEntity;

public interface BookshopRepository extends JpaRepository<BookshopJpaEntity, Long> {

	@EntityGraph(attributePaths = {"books"})
	List<BookshopJpaEntity>findAll();
	
	@EntityGraph(attributePaths = {"books"})
	Page<BookshopJpaEntity> findAll(Pageable pageable);
	
	@EntityGraph(attributePaths = {"books"})
	Optional<BookshopJpaEntity> findById(Long id);
	
	boolean existsByName(String name);
	
	List<BookshopJpaEntity> findByNameAndBookshopIdNot(String name, Long id);
}

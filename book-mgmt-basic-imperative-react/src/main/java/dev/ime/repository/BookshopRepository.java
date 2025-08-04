package dev.ime.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.ime.entity.BookshopJpaEntity;

public interface BookshopRepository extends JpaRepository<BookshopJpaEntity, Long> {

	@EntityGraph(attributePaths = {"books"})
	List<BookshopJpaEntity>findAll();
	
	@EntityGraph(attributePaths = {"books"})
	Page<BookshopJpaEntity> findAll(Pageable pageable);
	
	@EntityGraph(attributePaths = {"books"})
	Optional<BookshopJpaEntity> findById(Long id);
}

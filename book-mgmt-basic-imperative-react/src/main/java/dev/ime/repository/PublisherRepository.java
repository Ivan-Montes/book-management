package dev.ime.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.ime.entity.PublisherJpaEntity;

public interface PublisherRepository extends JpaRepository<PublisherJpaEntity,Long>{

	@EntityGraph(attributePaths = {"books", "books.genre"})
	List<PublisherJpaEntity>findAll();
	
	@EntityGraph(attributePaths = {"books", "books.genre"})
	Page<PublisherJpaEntity> findAll(Pageable pageable);
	
	@EntityGraph(attributePaths = {"books", "books.genre"})
	Optional<PublisherJpaEntity> findById(Long id);
}

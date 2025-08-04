package dev.ime.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.ime.entity.BookJpaEntity;

public interface BookRepository extends JpaRepository<BookJpaEntity, Long>{

    @EntityGraph(attributePaths = {"authors", "bookshops", "publisher", "genre"})
    List<BookJpaEntity> findAll();

    @EntityGraph(attributePaths = {"authors", "bookshops", "publisher", "genre"})
    Page<BookJpaEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"authors", "bookshops", "publisher", "genre"})
    Optional<BookJpaEntity> findById(Long id);
}

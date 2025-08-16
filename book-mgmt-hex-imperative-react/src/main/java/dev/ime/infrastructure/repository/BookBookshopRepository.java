package dev.ime.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.ime.infrastructure.entity.BookBookshopId;
import dev.ime.infrastructure.entity.BookBookshopJpaEntity;

public interface BookBookshopRepository  extends JpaRepository<BookBookshopJpaEntity, BookBookshopId>{

    @EntityGraph(attributePaths = {"book", "book.genre", "bookshop"})
    List<BookBookshopJpaEntity> findAll();

    @EntityGraph(attributePaths = {"book", "book.genre", "bookshop"})
    Page<BookBookshopJpaEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"book", "book.genre", "bookshop"})
    Optional<BookBookshopJpaEntity> findById(BookBookshopId id);
}
package dev.ime.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.common.mapper.BookMapper;
import dev.ime.domain.model.Book;
import dev.ime.domain.port.outbound.BookSpecificReadRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.infrastructure.entity.BookJpaEntity;
import dev.ime.infrastructure.repository.BookRepository;

@Repository
@Qualifier("bookReadRepositoryAdapter")
public class BookReadRepositoryAdapter implements ReadRepositoryPort<Book>, BookSpecificReadRepositoryPort {

	private final BookRepository bookRepository;
	private final BookMapper mapper;	

	public BookReadRepositoryAdapter(BookRepository bookRepository, BookMapper mapper) {
		super();
		this.bookRepository = bookRepository;
		this.mapper = mapper;
	}

	@Override
	public Page<Book> findAll(Pageable pageable) {

		Page<BookJpaEntity> page = bookRepository.findAll(pageable);
		List<Book> contentList = page.getContent().stream().map(mapper::fromJpaToDomain).toList();

		return new PageImpl<>(contentList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<Book> findById(Long id) {

		return bookRepository.findById(id).map(mapper::fromJpaToDomain);
	}

	@Override
	public boolean existsByIsbn(String isbn) {

		return bookRepository.existsByIsbn(isbn);
	}

	@Override
	public boolean findByIsbnAndIdNot(String isbn, Long id) {

		return !bookRepository.findByIsbnAndBookIdNot(isbn, id).isEmpty();
	}	
}

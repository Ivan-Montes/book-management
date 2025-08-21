package dev.ime.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.common.mapper.BookshopMapper;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;
import dev.ime.infrastructure.entity.BookshopJpaEntity;
import dev.ime.infrastructure.repository.BookshopRepository;

@Repository
@Qualifier("bookshopReadRepositoryAdapter")
public class BookshopReadRepositoryAdapter implements ReadRepositoryPort<Bookshop>, RequestByNameReadRepositoryPort {

	private final BookshopRepository bookshopRepository;
	private final BookshopMapper mapper;
	
	public BookshopReadRepositoryAdapter(BookshopRepository bookshopRepository, BookshopMapper mapper) {
		super();
		this.bookshopRepository = bookshopRepository;
		this.mapper = mapper;
	}

	@Override
	public Page<Bookshop> findAll(Pageable pageable) {

		Page<BookshopJpaEntity> page = bookshopRepository.findAll(pageable);
		List<Bookshop> contentList = page.getContent()
				.stream()
				.map(mapper::fromJpaToDomain)
				.toList();
		
	    return new PageImpl<>(contentList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<Bookshop> findById(Long id) {
		
		return bookshopRepository.findById(id)
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public boolean existsByName(String name) {

		return bookshopRepository.existsByName(name);
	}

	@Override
	public boolean findByNameAndIdNot(String name, Long id) {

		return !bookshopRepository.findByNameAndBookshopIdNot(name, id).isEmpty();
	}	
}

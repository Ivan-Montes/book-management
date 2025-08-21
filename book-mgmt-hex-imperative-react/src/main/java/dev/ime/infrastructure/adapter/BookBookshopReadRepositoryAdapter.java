package dev.ime.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.common.mapper.BookBookshopMapper;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.port.outbound.CompositeIdReadRepositoryPort;
import dev.ime.infrastructure.entity.BookBookshopId;
import dev.ime.infrastructure.entity.BookBookshopJpaEntity;
import dev.ime.infrastructure.repository.BookBookshopRepository;

@Repository
public class BookBookshopReadRepositoryAdapter implements CompositeIdReadRepositoryPort<BookBookshop> {

	private final BookBookshopRepository bookBookshopRepository;
    private final BookBookshopMapper mapper;
    
	public BookBookshopReadRepositoryAdapter(BookBookshopRepository bookBookshopRepository, BookBookshopMapper mapper) {
		super();
		this.bookBookshopRepository = bookBookshopRepository;
		this.mapper = mapper;
	}

	@Override
	public Page<BookBookshop> findAll(Pageable pageable) {

		Page<BookBookshopJpaEntity> page = bookBookshopRepository.findAll(pageable);
		List<BookBookshop> contentList = page.getContent()
				.stream()
				.map(mapper::fromJpaToDomain)
				.toList();
		
	    return new PageImpl<>(contentList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<BookBookshop> findById(Long bookId, Long bookshopId) {
		
		BookBookshopId id = new BookBookshopId(bookId, bookshopId);
		return bookBookshopRepository.findById(id)
				.map(mapper::fromJpaToDomain);
	}
}

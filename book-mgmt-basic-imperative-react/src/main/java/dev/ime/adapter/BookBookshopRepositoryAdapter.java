package dev.ime.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.entity.BookBookshopId;
import dev.ime.entity.BookBookshopJpaEntity;
import dev.ime.mapper.BookBookshopMapper;
import dev.ime.model.BookBookshop;
import dev.ime.port.CompositeIdRepositoryPort;
import dev.ime.repository.BookBookshopRepository;

@Repository
public class BookBookshopRepositoryAdapter implements CompositeIdRepositoryPort<BookBookshop> {

	private final BookBookshopRepository bookBookshopRepository;
    private final BookBookshopMapper mapper;
    
	public BookBookshopRepositoryAdapter(BookBookshopRepository bookBookshopRepository, BookBookshopMapper mapper) {
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
	public Optional<BookBookshop> findById(Long id01, Long id02) {
		
		BookBookshopId id = new BookBookshopId(id01, id02);
		return bookBookshopRepository.findById(id)
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public Optional<BookBookshop> save(BookBookshop item) {
		
		Long bookId = item.getBook().getBookId();
		Long bookshopId = item.getBookshop().getBookshopId();
		BookBookshopId id = new BookBookshopId(bookId, bookshopId);
		
		Optional<BookBookshopJpaEntity> opt = bookBookshopRepository.findById(id);
		if (!opt.isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.ofNullable(bookBookshopRepository.save(mapper.fromDomainToJpa(item)))
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public Optional<BookBookshop> update(BookBookshop item) {		
		
		Long bookId = item.getBook().getBookId();
		Long bookshopId = item.getBookshop().getBookshopId();
		BookBookshopId id = new BookBookshopId(bookId, bookshopId);

		Optional<BookBookshopJpaEntity> opt = bookBookshopRepository.findById(id);
		if (opt.isEmpty()) {
			return Optional.empty();
		}
		BookBookshopJpaEntity jpaEntity = opt.get();
		jpaEntity.setPrice(item.getPrice());
		jpaEntity.setUnits(item.getUnits());
		
		return Optional.ofNullable(bookBookshopRepository.save(jpaEntity))
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public boolean deleteById(Long id01, Long id02) {
		
		BookBookshopId id = new BookBookshopId(id01, id02);
		bookBookshopRepository.deleteById(id);	
		return bookBookshopRepository.findById(id).isEmpty();
	}
}

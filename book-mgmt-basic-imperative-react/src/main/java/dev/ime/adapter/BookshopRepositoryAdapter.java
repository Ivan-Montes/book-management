package dev.ime.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.entity.BookshopJpaEntity;
import dev.ime.mapper.BookshopMapper;
import dev.ime.model.Bookshop;
import dev.ime.port.RepositoryPort;
import dev.ime.repository.BookshopRepository;

@Repository
public class BookshopRepositoryAdapter implements RepositoryPort<Bookshop> {

	private final BookshopRepository bookshopRepository;
	private final BookshopMapper mapper;
	
	public BookshopRepositoryAdapter(BookshopRepository bookshopRepository, BookshopMapper mapper) {
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
	public Optional<Bookshop> save(Bookshop item) {
		
		return Optional.ofNullable(bookshopRepository.save(mapper.fromDomainToJpa(item)))
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public Optional<Bookshop> update(Bookshop item) {
		
		Optional<BookshopJpaEntity> opt = bookshopRepository.findById(item.getBookshopId());
		if (opt.isEmpty()) {
			return Optional.empty();
		}
		BookshopJpaEntity jpaEntity = opt.get();
		jpaEntity.setName(item.getName());
		
		bookshopRepository.save(jpaEntity);
	    return bookshopRepository.findById(item.getBookshopId())
	            .map(mapper::fromJpaToDomain);
	}

	@Override
	public boolean deleteById(Long id) {

		bookshopRepository.deleteById(id);	
		return bookshopRepository.findById(id).isEmpty();
	}
}

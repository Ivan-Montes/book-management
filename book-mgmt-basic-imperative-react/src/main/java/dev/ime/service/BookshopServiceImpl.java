package dev.ime.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.common.GlobalConstants;
import dev.ime.dto.BookshopDto;
import dev.ime.exception.ResourceNotFoundException;
import dev.ime.mapper.BookshopMapper;
import dev.ime.model.Bookshop;
import dev.ime.port.RepositoryPort;
import dev.ime.port.ServicePort;

@Service
public class BookshopServiceImpl implements ServicePort<BookshopDto>{

	private final RepositoryPort<Bookshop> bookshopRepositoryAdapter;
	private final BookshopMapper mapper;
	
	public BookshopServiceImpl(RepositoryPort<Bookshop> bookshopRepositoryAdapter, BookshopMapper mapper) {
		super();
		this.bookshopRepositoryAdapter = bookshopRepositoryAdapter;
		this.mapper = mapper;
	}

	@Override
	public Page<BookshopDto> getAll(Pageable pageable) {

		Page<Bookshop> page = bookshopRepositoryAdapter.findAll(pageable);
		List<BookshopDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<BookshopDto> getById(Long id) {
		
		return Optional.ofNullable(bookshopRepositoryAdapter.findById(id).map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.BS_ID, String.valueOf(id)))));
	}

	@Override
	public Optional<BookshopDto> create(BookshopDto item) {
		
		return Optional.ofNullable(bookshopRepositoryAdapter.save(mapper.fromDtoToDomain(item))
				.map(mapper::fromDomainToDto).orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.BS_CAT, String.valueOf(item)))));
	}

	@Override
	public Optional<BookshopDto> update(Long id, BookshopDto item) {

		Bookshop bookshop = mapper.fromDtoToDomain(item);
		bookshop.setBookshopId(id);

	    Bookshop updated = bookshopRepositoryAdapter.update(bookshop)
	        .orElseThrow(() -> new ResourceNotFoundException(
	            Map.of(GlobalConstants.BS_ID, String.valueOf(id))
	        ));

	    BookshopDto dto = mapper.fromDomainToDto(updated);

	    return Optional.of(dto);
	}

	@Override
	public boolean deleteById(Long id) {

		return bookshopRepositoryAdapter.deleteById(id);
	}
}

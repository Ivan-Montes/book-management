package dev.ime.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.common.GlobalConstants;
import dev.ime.dto.BookBookshopDto;
import dev.ime.exception.DuplicatedEntityException;
import dev.ime.exception.ResourceNotFoundException;
import dev.ime.mapper.BookBookshopMapper;
import dev.ime.model.BookBookshop;
import dev.ime.port.CompositeIdRepositoryPort;
import dev.ime.port.CompositeIdServicePort;

@Service
public class BookBookshopServiceImpl implements CompositeIdServicePort<BookBookshopDto>{
	
	private final CompositeIdRepositoryPort<BookBookshop> bookBookshopRespositoryAdapter;
	private final BookBookshopMapper mapper;
	
	public BookBookshopServiceImpl(CompositeIdRepositoryPort<BookBookshop> bookBookshopRespositoryAdapter,
			BookBookshopMapper mapper) {
		super();
		this.bookBookshopRespositoryAdapter = bookBookshopRespositoryAdapter;
		this.mapper = mapper;
	}

	@Override
	public Page<BookBookshopDto> getAll(Pageable pageable) {
		
		Page<BookBookshop> page = bookBookshopRespositoryAdapter.findAll(pageable);
		List<BookBookshopDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<BookBookshopDto> getById(Long id01, Long id02) {

		return Optional.ofNullable(bookBookshopRespositoryAdapter.findById(id01, id02).map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(id01), GlobalConstants.BS_ID, String.valueOf(id02)))));
	}

	@Override
	public Optional<BookBookshopDto> create(BookBookshopDto item) {

		return Optional.ofNullable(bookBookshopRespositoryAdapter.save(mapper.fromDtoToDomain(item))
				.map(mapper::fromDomainToDto).orElseThrow(() -> new DuplicatedEntityException(Map.of(GlobalConstants.BBS_CAT, String.valueOf(item)))));
	}

	@Override
	public Optional<BookBookshopDto> update(Long id01, Long id02, BookBookshopDto item) {

	    BookBookshop bookBookshop = mapper.fromDtoToDomain(item);
	    bookBookshop.getBook().setBookId(id01);
	    bookBookshop.getBookshop().setBookshopId(id02);

	    BookBookshop updated = bookBookshopRespositoryAdapter.update(bookBookshop)
	        .orElseThrow(() -> new ResourceNotFoundException(
	            Map.of(GlobalConstants.BOOK_ID, String.valueOf(id01), GlobalConstants.BS_ID, String.valueOf(id02))
	        ));

	    BookBookshopDto dto = mapper.fromDomainToDto(updated);

	    return Optional.of(dto);
	}

	@Override
	public boolean deleteById(Long id01, Long id02) {
		
		return bookBookshopRespositoryAdapter.deleteById(id01, id02);
	}
}

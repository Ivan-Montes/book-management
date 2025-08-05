package dev.ime.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.common.GlobalConstants;
import dev.ime.dto.BookDto;
import dev.ime.exception.ResourceNotFoundException;
import dev.ime.mapper.BookMapper;
import dev.ime.model.Book;
import dev.ime.port.RepositoryPort;
import dev.ime.port.ServicePort;

@Service
public class BookServiceImpl implements ServicePort<BookDto> {

	private final RepositoryPort<Book> bookRepositoryAdapter;
	private final BookMapper mapper;
	
	public BookServiceImpl(RepositoryPort<Book> bookRepositoryAdapter, BookMapper mapper) {
		super();
		this.bookRepositoryAdapter = bookRepositoryAdapter;
		this.mapper = mapper;
	}

	@Override
	public Page<BookDto> getAll(Pageable pageable) {
		
		Page<Book> page = bookRepositoryAdapter.findAll(pageable);
		List<BookDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<BookDto> getById(Long id) {

		return Optional.ofNullable(bookRepositoryAdapter.findById(id).map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(id)))));
	}

	@Override
	public Optional<BookDto> create(BookDto item) {

		return Optional.ofNullable(bookRepositoryAdapter.save(mapper.fromDtoToDomain(item))
				.map(mapper::fromDomainToDto).orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_CAT, String.valueOf(item)))));
	}

	@Override
	public Optional<BookDto> update(Long id, BookDto item) {

	    Book book = mapper.fromDtoToDomain(item);
	    book.setBookId(id);

	    Book updated = bookRepositoryAdapter.update(book)
	        .orElseThrow(() -> new ResourceNotFoundException(
	            Map.of(GlobalConstants.BOOK_ID, String.valueOf(id))
	        ));

	    BookDto dto = mapper.fromDomainToDto(updated);

	    return Optional.of(dto);
	}

	@Override
	public boolean deleteById(Long id) {
		
		return bookRepositoryAdapter.deleteById(id);
	}
}
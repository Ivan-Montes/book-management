package dev.ime.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.BookDto;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.query.GetAllBookQuery;
import dev.ime.application.usecases.query.GetByIdBookQuery;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.BookMapper;
import dev.ime.domain.model.Book;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;

@Service
public class BookQueryService implements QueryServicePort<BookDto> {

	private final QueryDispatcher queryDispatcher;
	private final BookMapper mapper;
	
	public BookQueryService(@Qualifier("bookQueryDispatcher")QueryDispatcher queryDispatcher, BookMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}

	@Override
	public Page<BookDto> getAll(Pageable pageable) {

		GetAllBookQuery getAllQuery = new GetAllBookQuery(pageable);
		QueryHandler<Page<Book>> handler = queryDispatcher.getQueryHandler(getAllQuery);
		Page<Book> page = handler.handle(getAllQuery);
		List<BookDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<BookDto> getById(Long id) {

		GetByIdBookQuery getByIdQuery = new GetByIdBookQuery(id);
		QueryHandler<Optional<Book>> handler = queryDispatcher.getQueryHandler(getByIdQuery);
		Optional<Book> opt = handler.handle(getByIdQuery);
		
		return Optional.of(opt
				.map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(id)))));
	}
}

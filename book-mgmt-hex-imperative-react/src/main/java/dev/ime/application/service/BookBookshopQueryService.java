package dev.ime.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.BookBookshopDto;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.query.GetAllBookBookshopQuery;
import dev.ime.application.usecases.query.GetByIdBookBookshopQuery;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.BookBookshopMapper;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.port.inbound.CompositeIdQueryServicePort;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.query.QueryHandler;

@Service
public class BookBookshopQueryService implements CompositeIdQueryServicePort<BookBookshopDto> {

	private final QueryDispatcher queryDispatcher;
	private final BookBookshopMapper mapper;
	
	public BookBookshopQueryService(@Qualifier("bookBookshopQueryDispatcher")QueryDispatcher queryDispatcher, BookBookshopMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}

	@Override
	public Page<BookBookshopDto> getAll(Pageable pageable) {
		
		GetAllBookBookshopQuery getAllQuery = new GetAllBookBookshopQuery(pageable);
		QueryHandler<Page<BookBookshop>> handler = queryDispatcher.getQueryHandler(getAllQuery);
		Page<BookBookshop> page = handler.handle(getAllQuery);
		List<BookBookshopDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<BookBookshopDto> getById(Long bookId, Long bookshopId) {

		GetByIdBookBookshopQuery getByIdQuery = new GetByIdBookBookshopQuery(bookId, bookshopId);
		QueryHandler<Optional<BookBookshop>> handler = queryDispatcher.getQueryHandler(getByIdQuery);
		Optional<BookBookshop> opt = handler.handle(getByIdQuery);
		
		return Optional.of(opt
				.map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.BBS_ID, String.valueOf(getByIdQuery)))));
	}
}

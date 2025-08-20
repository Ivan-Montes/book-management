package dev.ime.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.BookshopDto;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.query.GetAllBookshopQuery;
import dev.ime.application.usecases.query.GetByIdBookshopQuery;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.BookshopMapper;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;

@Service
public class BookshopQueryService implements QueryServicePort<BookshopDto> {

	private final QueryDispatcher queryDispatcher;
	private final BookshopMapper mapper;
	
	public BookshopQueryService(@Qualifier("bookshopQueryDispatcher")QueryDispatcher queryDispatcher, BookshopMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}

	@Override
	public Page<BookshopDto> getAll(Pageable pageable) {
		
		GetAllBookshopQuery getAllQuery = new GetAllBookshopQuery(pageable);
		QueryHandler<Page<Bookshop>> handler = queryDispatcher.getQueryHandler(getAllQuery);
		Page<Bookshop> page = handler.handle(getAllQuery);
		List<BookshopDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<BookshopDto> getById(Long id) {

		GetByIdBookshopQuery getByIdQuery = new GetByIdBookshopQuery(id);
		QueryHandler<Optional<Bookshop>> handler = queryDispatcher.getQueryHandler(getByIdQuery);
		Optional<Bookshop> opt = handler.handle(getByIdQuery);
		
		return Optional.of(opt
				.map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.BS_ID, String.valueOf(id)))));
	}
}

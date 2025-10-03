package dev.ime.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.BookshopDto;
import dev.ime.application.usecases.query.GetAllBookshopQuery;
import dev.ime.application.usecases.query.GetByIdBookshopQuery;
import dev.ime.common.mapper.BookshopMapper;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

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
	public Mono<Page<BookshopDto>> getAll(Pageable pageable) {
		
		return Mono.just(new GetAllBookshopQuery(pageable)).flatMap(this::processGetAllQuery);
	}

	private Mono<Page<BookshopDto>> processGetAllQuery(GetAllBookshopQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Page<Bookshop>>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query))
				.zipWhen(page -> convertContent(page),
				         (page, dtoList) -> new PageImpl<>(dtoList, query.pageable(), page.getTotalElements()));
	}
	
	private Mono<List<BookshopDto>> convertContent(Page<Bookshop> page) {
		
		return Mono.just(page)
				.map(Page::getContent)
				.map( item -> item.stream()
						    .map(mapper::fromDomainToDto)
						    .toList());
	}
	
	@Override
	public Mono<BookshopDto> getById(UUID id) {
		
		return Mono.just(new GetByIdBookshopQuery(id))
				.flatMap(this::processGetByIdQuery);
	}

	private Mono<BookshopDto> processGetByIdQuery(GetByIdBookshopQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Bookshop>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query)).map(mapper::fromDomainToDto);
	}
}

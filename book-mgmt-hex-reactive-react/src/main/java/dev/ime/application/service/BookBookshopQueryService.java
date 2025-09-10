package dev.ime.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.BookBookshopDto;
import dev.ime.application.usecases.query.GetAllBookBookshopQuery;
import dev.ime.application.usecases.query.GetByIdBookBookshopQuery;
import dev.ime.common.mapper.BookBookshopMapper;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.port.inbound.CompositeIdQueryServicePort;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

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
	public Mono<Page<BookBookshopDto>> getAll(Pageable pageable) {
		
		return Mono.just(new GetAllBookBookshopQuery(pageable)).flatMap(this::processGetAllQuery);
	}

	private Mono<Page<BookBookshopDto>> processGetAllQuery(GetAllBookBookshopQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Page<BookBookshop>>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query))
				.zipWhen(page -> convertContent(page),
				         (page, dtoList) -> new PageImpl<>(dtoList, query.pageable(), page.getTotalElements()));
	}
	
	private Mono<List<BookBookshopDto>> convertContent(Page<BookBookshop> page) {
		
		return Mono.just(page)
				.map(Page::getContent)
				.map( item -> item.stream()
						    .map(mapper::fromDomainToDto)
						    .toList());
	}	

	@Override
	public Mono<BookBookshopDto> getById(UUID id01, UUID id02) {

		return Mono.just(new GetByIdBookBookshopQuery(id01, id02))
				.flatMap(this::processGetByIdQuery);
	}

	private Mono<BookBookshopDto> processGetByIdQuery(GetByIdBookBookshopQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<BookBookshop>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query)).map(mapper::fromDomainToDto);
	}
}

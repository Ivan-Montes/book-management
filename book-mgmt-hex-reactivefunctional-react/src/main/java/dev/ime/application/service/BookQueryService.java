package dev.ime.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.BookDto;
import dev.ime.application.usecases.query.GetAllBookQuery;
import dev.ime.application.usecases.query.GetByIdBookQuery;
import dev.ime.common.mapper.BookMapper;
import dev.ime.domain.model.Book;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

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
	public Mono<Page<BookDto>> getAll(Pageable pageable) {
		
		return Mono.just(new GetAllBookQuery(pageable)).flatMap(this::processGetAllQuery);
	}

	private Mono<Page<BookDto>> processGetAllQuery(GetAllBookQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Page<Book>>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query))
				.zipWhen(page -> convertContent(page),
				         (page, dtoList) -> new PageImpl<>(dtoList, query.pageable(), page.getTotalElements()));
	}
	
	private Mono<List<BookDto>> convertContent(Page<Book> page) {
		
		return Mono.just(page)
				.map(Page::getContent)
				.map( item -> item.stream()
						    .map(mapper::fromDomainToDto)
						    .toList());
	}
	
	@Override
	public Mono<BookDto> getById(UUID id) {
		
		return Mono.just(new GetByIdBookQuery(id))
				.flatMap(this::processGetByIdQuery);
	}

	private Mono<BookDto> processGetByIdQuery(GetByIdBookQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Book>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query)).map(mapper::fromDomainToDto);
	}
}

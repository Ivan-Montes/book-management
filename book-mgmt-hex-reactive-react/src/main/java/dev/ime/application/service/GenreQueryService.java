package dev.ime.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.GenreDto;
import dev.ime.application.usecases.query.GetAllGenreQuery;
import dev.ime.application.usecases.query.GetByIdGenreQuery;
import dev.ime.common.mapper.GenreMapper;
import dev.ime.domain.model.Genre;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Service
public class GenreQueryService implements QueryServicePort<GenreDto> {

	private final QueryDispatcher queryDispatcher;
	private final GenreMapper mapper;
	
	public GenreQueryService(@Qualifier("genreQueryDispatcher")QueryDispatcher queryDispatcher, GenreMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}

	@Override
	public Mono<Page<GenreDto>> getAll(Pageable pageable) {
		
		return Mono.just(new GetAllGenreQuery(pageable)).flatMap(this::processGetAllQuery);
	}

	private Mono<Page<GenreDto>> processGetAllQuery(GetAllGenreQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Page<Genre>>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query))
				.zipWhen(page -> convertContent(page),
				         (page, dtoList) -> new PageImpl<>(dtoList, query.pageable(), page.getTotalElements()));
	}
	
	private Mono<List<GenreDto>> convertContent(Page<Genre> page) {
		
		return Mono.just(page)
				.map(Page::getContent)
				.map( item -> item.stream()
						    .map(mapper::fromDomainToDto)
						    .toList());
	}
	
	@Override
	public Mono<GenreDto> getById(UUID id) {
		
		return Mono.just(new GetByIdGenreQuery(id))
				.flatMap(this::processGetByIdQuery);
	}

	private Mono<GenreDto> processGetByIdQuery(GetByIdGenreQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Genre>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query)).map(mapper::fromDomainToDto);
	}
}

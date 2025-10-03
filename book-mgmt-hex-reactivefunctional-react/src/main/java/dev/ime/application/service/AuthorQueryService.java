package dev.ime.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.AuthorDto;
import dev.ime.application.usecases.query.GetAllAuthorQuery;
import dev.ime.application.usecases.query.GetByIdAuthorQuery;
import dev.ime.common.mapper.AuthorMapper;
import dev.ime.domain.model.Author;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Service
public class AuthorQueryService implements QueryServicePort<AuthorDto> {

	private final QueryDispatcher queryDispatcher;
	private final AuthorMapper mapper;
	
	public AuthorQueryService(@Qualifier("authorQueryDispatcher")QueryDispatcher queryDispatcher, AuthorMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}

	@Override
	public Mono<Page<AuthorDto>> getAll(Pageable pageable) {
		
		return Mono.just(new GetAllAuthorQuery(pageable)).flatMap(this::processGetAllQuery);
	}

	private Mono<Page<AuthorDto>> processGetAllQuery(GetAllAuthorQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Page<Author>>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query))
				.zipWhen(page -> convertContent(page),
				         (page, dtoList) -> new PageImpl<>(dtoList, query.pageable(), page.getTotalElements()));
	}
	
	private Mono<List<AuthorDto>> convertContent(Page<Author> page) {
		
		return Mono.just(page)
				.map(Page::getContent)
				.map( item -> item.stream()
						    .map(mapper::fromDomainToDto)
						    .toList());
	}
	
	@Override
	public Mono<AuthorDto> getById(UUID id) {
		
		return Mono.just(new GetByIdAuthorQuery(id))
				.flatMap(this::processGetByIdQuery);
	}

	private Mono<AuthorDto> processGetByIdQuery(GetByIdAuthorQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Author>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query)).map(mapper::fromDomainToDto);
	}
}

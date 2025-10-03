package dev.ime.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.PublisherDto;
import dev.ime.application.usecases.query.GetAllPublisherQuery;
import dev.ime.application.usecases.query.GetByIdPublisherQuery;
import dev.ime.common.mapper.PublisherMapper;
import dev.ime.domain.model.Publisher;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Service
public class PublisherQueryService implements QueryServicePort<PublisherDto> {

	private final QueryDispatcher queryDispatcher;
	private final PublisherMapper mapper;
	
	public PublisherQueryService(@Qualifier("publisherQueryDispatcher")QueryDispatcher queryDispatcher, PublisherMapper mapper) {
		super();
		this.queryDispatcher = queryDispatcher;
		this.mapper = mapper;
	}
	
	@Override
	public Mono<Page<PublisherDto>> getAll(Pageable pageable) {
		
		return Mono.just(new GetAllPublisherQuery(pageable)).flatMap(this::processGetAllQuery);
	}

	private Mono<Page<PublisherDto>> processGetAllQuery(GetAllPublisherQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Page<Publisher>>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query))
				.zipWhen(page -> convertContent(page),
				         (page, dtoList) -> new PageImpl<>(dtoList, query.pageable(), page.getTotalElements()));
	}
	
	private Mono<List<PublisherDto>> convertContent(Page<Publisher> page) {
		
		return Mono.just(page)
				.map(Page::getContent)
				.map( item -> item.stream()
						    .map(mapper::fromDomainToDto)
						    .toList());
	}
	
	@Override
	public Mono<PublisherDto> getById(UUID id) {
		
		return Mono.just(new GetByIdPublisherQuery(id))
				.flatMap(this::processGetByIdQuery);
	}

	private Mono<PublisherDto> processGetByIdQuery(GetByIdPublisherQuery query) {

		return Mono.fromSupplier(() -> {
			QueryHandler<Mono<Publisher>> handler = queryDispatcher.getQueryHandler(query);
			return handler;
		}).flatMap(handler -> handler.handle(query)).map(mapper::fromDomainToDto);
	}
}

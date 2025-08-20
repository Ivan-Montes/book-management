package dev.ime.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.PublisherDto;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.query.GetAllPublisherQuery;
import dev.ime.application.usecases.query.GetByIdPublisherQuery;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.PublisherMapper;
import dev.ime.domain.model.Publisher;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;

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
	public Page<PublisherDto> getAll(Pageable pageable) {

		GetAllPublisherQuery getAllQuery = new GetAllPublisherQuery(pageable);
		QueryHandler<Page<Publisher>> handler = queryDispatcher.getQueryHandler(getAllQuery);
		Page<Publisher> page = handler.handle(getAllQuery);
		List<PublisherDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<PublisherDto> getById(Long id) {

		GetByIdPublisherQuery getByIdQuery = new GetByIdPublisherQuery(id);
		QueryHandler<Optional<Publisher>> handler = queryDispatcher.getQueryHandler(getByIdQuery);
		Optional<Publisher> opt = handler.handle(getByIdQuery);
		
		return Optional.of(opt
				.map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.PUBLI_ID, String.valueOf(id)))));
	}
}

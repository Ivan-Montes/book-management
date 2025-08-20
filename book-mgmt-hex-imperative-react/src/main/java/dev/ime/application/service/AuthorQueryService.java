package dev.ime.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.AuthorDto;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.query.GetAllAuthorQuery;
import dev.ime.application.usecases.query.GetByIdAuthorQuery;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.AuthorMapper;
import dev.ime.domain.model.Author;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;

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
	public Page<AuthorDto> getAll(Pageable pageable) {
		
		GetAllAuthorQuery getAllQuery = new GetAllAuthorQuery(pageable);
		QueryHandler<Page<Author>> handler = queryDispatcher.getQueryHandler(getAllQuery);
		Page<Author> page = handler.handle(getAllQuery);
		List<AuthorDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<AuthorDto> getById(Long id) {
		
		GetByIdAuthorQuery getByIdQuery = new GetByIdAuthorQuery(id);
		QueryHandler<Optional<Author>> handler = queryDispatcher.getQueryHandler(getByIdQuery);
		Optional<Author> opt = handler.handle(getByIdQuery);
		
		return Optional.of(opt
				.map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.AUTHOR_ID, String.valueOf(id)))));
	}
}

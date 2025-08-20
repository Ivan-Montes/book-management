package dev.ime.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.GenreDto;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.query.GetAllGenreQuery;
import dev.ime.application.usecases.query.GetByIdGenreQuery;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.GenreMapper;
import dev.ime.domain.model.Genre;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.port.inbound.QueryServicePort;
import dev.ime.domain.query.QueryHandler;

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
	public Page<GenreDto> getAll(Pageable pageable) {

		GetAllGenreQuery getAllQuery = new GetAllGenreQuery(pageable);
		QueryHandler<Page<Genre>> handler = queryDispatcher.getQueryHandler(getAllQuery);
		Page<Genre> page = handler.handle(getAllQuery);
		List<GenreDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<GenreDto> getById(Long id) {

		GetByIdGenreQuery getByIdQuery = new GetByIdGenreQuery(id);
		QueryHandler<Optional<Genre>> handler = queryDispatcher.getQueryHandler(getByIdQuery);
		Optional<Genre> opt = handler.handle(getByIdQuery);
		
		return Optional.of(opt
				.map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_ID, String.valueOf(id)))));
	}
}

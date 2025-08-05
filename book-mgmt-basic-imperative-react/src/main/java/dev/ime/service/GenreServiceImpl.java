package dev.ime.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.common.GlobalConstants;
import dev.ime.dto.GenreDto;
import dev.ime.exception.ResourceNotFoundException;
import dev.ime.mapper.GenreMapper;
import dev.ime.model.Genre;
import dev.ime.port.RepositoryPort;
import dev.ime.port.ServicePort;

@Service
public class GenreServiceImpl implements ServicePort<GenreDto> {

	private final RepositoryPort<Genre> genreRepositoryAdapter;
	private final GenreMapper mapper;

	public GenreServiceImpl(RepositoryPort<Genre> genreRepositoryAdapter, GenreMapper mapper) {
		super();
		this.genreRepositoryAdapter = genreRepositoryAdapter;
		this.mapper = mapper;
	}

	@Override
	public Page<GenreDto> getAll(Pageable pageable) {
		
		Page<Genre> page = genreRepositoryAdapter.findAll(pageable);
		List<GenreDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<GenreDto> getById(Long id) {

		return Optional.ofNullable(genreRepositoryAdapter.findById(id).map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_ID, String.valueOf(id)))));
	}

	@Override
	public Optional<GenreDto> create(GenreDto item) {

		return Optional.ofNullable(genreRepositoryAdapter.save(mapper.fromDtoToDomain(item))
				.map(mapper::fromDomainToDto).orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_CAT, String.valueOf(item)))));
	}

	@Override
	public Optional<GenreDto> update(Long id, GenreDto item) {

	    Genre genre = mapper.fromDtoToDomain(item);
	    genre.setGenreId(id);

	    Genre updated = genreRepositoryAdapter.update(genre)
	        .orElseThrow(() -> new ResourceNotFoundException(
	            Map.of(GlobalConstants.GENRE_ID, String.valueOf(id))
	        ));

	    GenreDto dto = mapper.fromDomainToDto(updated);

	    return Optional.of(dto);
	}

	@Override
	public boolean deleteById(Long id) {
		
		return genreRepositoryAdapter.deleteById(id);
	}
}

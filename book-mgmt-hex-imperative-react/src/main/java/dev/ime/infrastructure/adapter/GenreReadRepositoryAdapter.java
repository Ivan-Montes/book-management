package dev.ime.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.common.mapper.GenreMapper;
import dev.ime.domain.model.Genre;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;
import dev.ime.infrastructure.entity.GenreJpaEntity;
import dev.ime.infrastructure.repository.GenreRepository;

@Repository
@Qualifier("genreReadRepositoryAdapter")
public class GenreReadRepositoryAdapter implements ReadRepositoryPort<Genre>, RequestByNameReadRepositoryPort {

	private final GenreRepository genreRepository;
    private final GenreMapper mapper;
    
	public GenreReadRepositoryAdapter(GenreRepository genreRepository, GenreMapper mapper) {
		super();
		this.genreRepository = genreRepository;
		this.mapper = mapper;
	}

	@Override
	public Page<Genre> findAll(Pageable pageable) {
		
		Page<GenreJpaEntity> page = genreRepository.findAll(pageable);
		List<Genre> contentList = page.getContent()
				.stream()
				.map(mapper::fromJpaToDomain)
				.toList();
		
	    return new PageImpl<>(contentList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<Genre> findById(Long id) {
		
		return genreRepository.findById(id)
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public boolean existsByName(String name) {
		
		return genreRepository.existsByName(name);
	}

	@Override
	public boolean findByNameAndIdNot(String name, Long id) {
		
		return !genreRepository.findByNameAndGenreIdNot(name, id).isEmpty();
	}
}

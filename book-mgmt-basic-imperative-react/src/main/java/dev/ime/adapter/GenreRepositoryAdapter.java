package dev.ime.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.entity.GenreJpaEntity;
import dev.ime.mapper.GenreMapper;
import dev.ime.model.Genre;
import dev.ime.port.RepositoryPort;
import dev.ime.repository.GenreRepository;

@Repository
public class GenreRepositoryAdapter implements RepositoryPort<Genre> {

	private final GenreRepository genreRepository;
    private final GenreMapper mapper;
    
	public GenreRepositoryAdapter(GenreRepository genreRepository, GenreMapper mapper) {
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
	public Optional<Genre> save(Genre item) {
		
		return Optional.ofNullable(genreRepository.save(mapper.fromDomainToJpa(item)))
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public Optional<Genre> update(Genre item) {
		
		Optional<GenreJpaEntity> opt = genreRepository.findById(item.getGenreId());
		if (opt.isEmpty()) {
			return Optional.empty();
		}
		GenreJpaEntity jpaEntity = opt.get();
		jpaEntity.setName(item.getName());
		jpaEntity.setDescription(item.getDescription());
		
		return Optional.ofNullable(genreRepository.save(jpaEntity))
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public boolean deleteById(Long id) {
		
		genreRepository.deleteById(id);	
		return genreRepository.findById(id).isEmpty();
	}
}

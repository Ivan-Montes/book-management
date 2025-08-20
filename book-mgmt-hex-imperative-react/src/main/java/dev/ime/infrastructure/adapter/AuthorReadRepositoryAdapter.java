package dev.ime.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.common.mapper.AuthorMapper;
import dev.ime.domain.model.Author;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.infrastructure.entity.AuthorJpaEntity;
import dev.ime.infrastructure.repository.AuthorRepository;

@Repository
@Qualifier("authorReadRepositoryAdapter")
public class AuthorReadRepositoryAdapter implements ReadRepositoryPort<Author> {

	private final AuthorRepository authorRepository;
	private final AuthorMapper mapper;
	
	public AuthorReadRepositoryAdapter(AuthorRepository authorRepository, AuthorMapper mapper) {
		super();
		this.authorRepository = authorRepository;
		this.mapper = mapper;
	}

	@Override
	public Page<Author> findAll(Pageable pageable) {

		Page<AuthorJpaEntity> page = authorRepository.findAllWithBooksAuthorsGenresPublishers(pageable);
		List<Author> contentList = page.getContent()
				.stream()
				.map(mapper::fromJpaToDomain)
				.toList();
		
	    return new PageImpl<>(contentList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<Author> findById(Long id) {

		return authorRepository.findByIdWithBooksAndAllRelations(id)
				.map(mapper::fromJpaToDomain);
	}
}

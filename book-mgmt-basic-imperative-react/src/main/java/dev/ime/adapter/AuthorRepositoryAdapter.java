package dev.ime.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.entity.AuthorJpaEntity;
import dev.ime.mapper.AuthorMapper;
import dev.ime.model.Author;
import dev.ime.port.RepositoryPort;
import dev.ime.repository.AuthorRepository;

@Repository
public class AuthorRepositoryAdapter implements RepositoryPort<Author> {

	private final AuthorRepository authorRepository;
	private final AuthorMapper mapper;
	
	public AuthorRepositoryAdapter(AuthorRepository authorRepository, AuthorMapper mapper) {
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

	@Override
	public Optional<Author> save(Author item) {

		return Optional.ofNullable(authorRepository.save(mapper.fromDomainToJpa(item)))
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public Optional<Author> update(Author item) {

		Optional<AuthorJpaEntity> opt = authorRepository.findById(item.getAuthorId());
		if (opt.isEmpty()) {
			return Optional.empty();
		}
		AuthorJpaEntity jpaEntity = opt.get();
		jpaEntity.setName(item.getName());
		jpaEntity.setSurname(item.getSurname());
		
		authorRepository.save(jpaEntity);
		return authorRepository.findByIdWithBooksAndAllRelations(item.getAuthorId())
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public boolean deleteById(Long id) {

		authorRepository.deleteById(id);	
		return authorRepository.findById(id).isEmpty();
	}
}

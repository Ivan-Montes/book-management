package dev.ime.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.entity.PublisherJpaEntity;
import dev.ime.mapper.PublisherMapper;
import dev.ime.model.Publisher;
import dev.ime.port.RepositoryPort;
import dev.ime.repository.PublisherRepository;

@Repository
public class PublisherRepositoryAdapter implements RepositoryPort<Publisher> {

	private final PublisherRepository publisherRepository;
    private final PublisherMapper mapper;    
    
	public PublisherRepositoryAdapter(PublisherRepository publisherRepository, PublisherMapper mapper) {
		super();
		this.publisherRepository = publisherRepository;
		this.mapper = mapper;
	}

	@Override
	public Page<Publisher> findAll(Pageable pageable) {

		Page<PublisherJpaEntity> page = publisherRepository.findAll(pageable);
		List<Publisher> contentList = page.getContent()
				.stream()
				.map(mapper::fromJpaToDomain)
				.toList();
		
	    return new PageImpl<>(contentList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<Publisher> findById(Long id) {
		
		return publisherRepository.findById(id)
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public Optional<Publisher> save(Publisher item) {
		
		return Optional.ofNullable(publisherRepository.save(mapper.fromDomainToJpa(item)))
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public Optional<Publisher> update(Publisher item) {
		
		Optional<PublisherJpaEntity> opt = publisherRepository.findById(item.getPublisherId());
		if (opt.isEmpty()) {
			return Optional.empty();
		}
		PublisherJpaEntity jpaEntity = opt.get();
		jpaEntity.setName(item.getName());
		
		return Optional.ofNullable(publisherRepository.save(jpaEntity))
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public boolean deleteById(Long id) {

		publisherRepository.deleteById(id);	
		return publisherRepository.findById(id).isEmpty();
	}
}

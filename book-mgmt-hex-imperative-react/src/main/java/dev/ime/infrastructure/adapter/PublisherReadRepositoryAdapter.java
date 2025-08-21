package dev.ime.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.common.mapper.PublisherMapper;
import dev.ime.domain.model.Publisher;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;
import dev.ime.infrastructure.entity.PublisherJpaEntity;
import dev.ime.infrastructure.repository.PublisherRepository;

@Repository
@Qualifier("publisherReadRepositoryAdapter")
public class PublisherReadRepositoryAdapter implements ReadRepositoryPort<Publisher>, RequestByNameReadRepositoryPort {

	private final PublisherRepository publisherRepository;
    private final PublisherMapper mapper;    
    
	public PublisherReadRepositoryAdapter(PublisherRepository publisherRepository,
			PublisherMapper mapper) {
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
	public boolean existsByName(String name) {

		return publisherRepository.existsByName(name);
	}

	@Override
	public boolean findByNameAndIdNot(String name, Long id) {

		return !publisherRepository.findByNameAndPublisherIdNot(name, id).isEmpty();
	}
}

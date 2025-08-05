package dev.ime.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.common.GlobalConstants;
import dev.ime.dto.PublisherDto;
import dev.ime.exception.ResourceNotFoundException;
import dev.ime.mapper.PublisherMapper;
import dev.ime.model.Publisher;
import dev.ime.port.RepositoryPort;
import dev.ime.port.ServicePort;

@Service
public class PublisherServiceImpl implements ServicePort<PublisherDto>{

	private final RepositoryPort<Publisher> publishRepositoryAdapter;
	private final PublisherMapper mapper;
	
	public PublisherServiceImpl(RepositoryPort<Publisher> publishRepositoryAdapter, PublisherMapper mapper) {
		super();
		this.publishRepositoryAdapter = publishRepositoryAdapter;
		this.mapper = mapper;
	}
	
	@Override
	public Page<PublisherDto> getAll(Pageable pageable) {
		
		Page<Publisher> page = publishRepositoryAdapter.findAll(pageable);
		List<PublisherDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}
	
	@Override
	public Optional<PublisherDto> getById(Long id) {

		return Optional.ofNullable(publishRepositoryAdapter.findById(id).map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.PUBLI_ID, String.valueOf(id)))));
	}
	
	@Override
	public Optional<PublisherDto> create(PublisherDto item) {

		return Optional.ofNullable(publishRepositoryAdapter.save(mapper.fromDtoToDomain(item))
				.map(mapper::fromDomainToDto).orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.PUBLI_CAT, String.valueOf(item)))));
	}
	
	@Override
	public Optional<PublisherDto> update(Long id, PublisherDto item) {

	    Publisher publisher = mapper.fromDtoToDomain(item);
	    publisher.setPublisherId(id);

	    Publisher updated = publishRepositoryAdapter.update(publisher)
	        .orElseThrow(() -> new ResourceNotFoundException(
	            Map.of(GlobalConstants.PUBLI_ID, String.valueOf(id))
	        ));

	    PublisherDto dto = mapper.fromDomainToDto(updated);

	    return Optional.of(dto);
	}
	
	@Override
	public boolean deleteById(Long id) {
		
		return publishRepositoryAdapter.deleteById(id);
	}
}

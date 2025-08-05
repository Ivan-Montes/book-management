package dev.ime.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ime.common.GlobalConstants;
import dev.ime.dto.AuthorDto;
import dev.ime.exception.ResourceNotFoundException;
import dev.ime.mapper.AuthorMapper;
import dev.ime.model.Author;
import dev.ime.port.RepositoryPort;
import dev.ime.port.ServicePort;

@Service
public class AuthorServiceImpl implements ServicePort<AuthorDto>{
	
	private final RepositoryPort<Author> authorRepositoryAdapter;
	private final AuthorMapper mapper;
	
	public AuthorServiceImpl(RepositoryPort<Author> authorRepositoryAdapter, AuthorMapper mapper) {
		super();
		this.authorRepositoryAdapter = authorRepositoryAdapter;
		this.mapper = mapper;
	}
	
	@Override
	public Page<AuthorDto> getAll(Pageable pageable) {

		Page<Author> page = authorRepositoryAdapter.findAll(pageable);
		List<AuthorDto> dtoList = page.getContent()
			    .stream()
			    .map(mapper::fromDomainToDto)
			    .toList();
		
	    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
	}
	
	@Override
	public Optional<AuthorDto> getById(Long id) {

		return Optional.ofNullable(authorRepositoryAdapter.findById(id).map(mapper::fromDomainToDto)
				.orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.AUTHOR_ID, String.valueOf(id)))));
	}
	
	@Override
	public Optional<AuthorDto> create(AuthorDto item) {

		return Optional.ofNullable(authorRepositoryAdapter.save(mapper.fromDtoToDomain(item))
				.map(mapper::fromDomainToDto).orElseThrow(() -> new ResourceNotFoundException(Map.of(GlobalConstants.AUTHOR_CAT, String.valueOf(item)))));
	}
	
	@Override
	public Optional<AuthorDto> update(Long id, AuthorDto item) {

	    Author author = mapper.fromDtoToDomain(item);
	    author.setAuthorId(id);

	    Author updated = authorRepositoryAdapter.update(author)
	        .orElseThrow(() -> new ResourceNotFoundException(
	            Map.of(GlobalConstants.AUTHOR_ID, String.valueOf(id))
	        ));

	    AuthorDto dto = mapper.fromDomainToDto(updated);

	    return Optional.of(dto);
	}
	
	@Override
	public boolean deleteById(Long id) {
		
		return authorRepositoryAdapter.deleteById(id);
	}	
}

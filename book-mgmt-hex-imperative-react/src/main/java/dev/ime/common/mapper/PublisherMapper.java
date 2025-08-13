package dev.ime.common.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.PublisherDto;
import dev.ime.domain.model.Book;
import dev.ime.domain.model.Genre;
import dev.ime.domain.model.Publisher;
import dev.ime.infrastructure.entity.BookJpaEntity;
import dev.ime.infrastructure.entity.GenreJpaEntity;
import dev.ime.infrastructure.entity.PublisherJpaEntity;

@Component
public class PublisherMapper implements GenericMapper<PublisherDto, Publisher, PublisherJpaEntity>{

	@Override
	public Publisher fromDtoToDomain(PublisherDto dto) {
		
		Publisher publisher = new Publisher();
		publisher.setPublisherId(dto.publisherId());
		publisher.setName(dto.name());
		
		return publisher;
	}

	@Override
	public PublisherJpaEntity fromDomainToJpa(Publisher dom) {
		
		return PublisherJpaEntity.builder()
				.publisherId(dom.getPublisherId())
				.name(dom.getName())
				.build();
	}

	@Override
	public Publisher fromJpaToDomain(PublisherJpaEntity jpa) {

		Publisher publisher = new Publisher();
		publisher.setPublisherId(jpa.getPublisherId());
		publisher.setName(jpa.getName());
		
		Set<Book> books = fromSetBookJpaToSetBookDom(jpa.getBooks(), publisher);
		publisher.setBooks(books);
		return publisher;
	}

	private Set<Book> fromSetBookJpaToSetBookDom(Set<BookJpaEntity>booksJpa, Publisher publisher) {
		
		if ( booksJpa == null ) {
			return new HashSet<>();
		}
		
		return booksJpa.stream()
				.map( e -> fromBookJpaToBookDom(e, publisher))
				.collect(Collectors.toSet());	
	}	
	
	private Book fromBookJpaToBookDom(BookJpaEntity bookJpa, Publisher publisher) {
		
		Genre genre = fromGenreJpaToGenreDom(bookJpa.getGenre());
		
		Book book = new Book();
		book.setBookId(bookJpa.getBookId());
		book.setIsbn(bookJpa.getIsbn());
		book.setTitle(bookJpa.getTitle());
		book.setCreationTimestamp(bookJpa.getCreationTimestamp());
		book.setUpdateTimestamp(bookJpa.getUpdateTimestamp());
		book.setPublisher(publisher);
		book.setGenre(genre);
		
		return book;		
	}

	private Genre fromGenreJpaToGenreDom(GenreJpaEntity genreJpa) {
		
		Genre genre = new Genre();
		genre.setGenreId(genreJpa.getGenreId());
		genre.setName(genreJpa.getName());
		genre.setBooks(new HashSet<>());
		
		return genre;
	}

	@Override
	public List<Publisher> fromListJpaToListDomain(List<PublisherJpaEntity> list) {

		if ( list == null ) {
			return new ArrayList<>();
		}

		return list.stream()
				.map(this::fromJpaToDomain)
				.toList();	
	}

	@Override
	public PublisherDto fromDomainToDto(Publisher dom) {
		
		return new PublisherDto(
				dom.getPublisherId(),
				dom.getName()
				);
	}

	@Override
	public List<PublisherDto> fromListDomainToListDto(List<Publisher> list) {

		if ( list == null ) {
			return new ArrayList<>();
		}

		return list.stream()
				.map(this::fromDomainToDto)
				.toList();	
	}
}

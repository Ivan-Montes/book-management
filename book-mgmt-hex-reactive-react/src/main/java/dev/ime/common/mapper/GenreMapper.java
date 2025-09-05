package dev.ime.common.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.GenreDto;
import dev.ime.domain.model.Book;
import dev.ime.domain.model.Genre;
import dev.ime.domain.model.Publisher;
import dev.ime.infrastructure.entity.BookJpaEntity;
import dev.ime.infrastructure.entity.GenreJpaEntity;
import dev.ime.infrastructure.entity.PublisherJpaEntity;

@Component
public class GenreMapper  implements GenericMapper<GenreDto, Genre, GenreJpaEntity> {

	public Genre fromDtoToDomain(GenreDto dto) {
		
		Genre genre = new Genre();
		genre.setGenreId(dto.genreId());
		genre.setName(dto.name());
		genre.setDescription(dto.description());
		
		return genre;
	}
	
	public GenreJpaEntity fromDomainToJpa(Genre dom) {		
		
		return GenreJpaEntity.builder()
				.genreId(dom.getGenreId())
				.name(dom.getName())
				.description(dom.getDescription())
				.build();
	}
	
	public List<Genre> fromListJpaToListDomain(List<GenreJpaEntity>list) {

		if ( list == null ) {
			return new ArrayList<>();
		}

		return list.stream()
				.map(this::fromJpaToDomain)
				.toList();	
	}
	
	public Genre fromJpaToDomain(GenreJpaEntity entity) {
		
		Genre genre = new Genre();
		genre.setGenreId(entity.getGenreId());
		genre.setName(entity.getName());
		genre.setDescription(entity.getDescription());

		Set<Book> books = fromSetBookJpaToSetBookDom(entity.getBooks(), genre);
		genre.setBooks(books);
		
		return genre;
	}
	
	private Set<Book> fromSetBookJpaToSetBookDom(Set<BookJpaEntity>booksJpa, Genre genre) {
		
		if ( booksJpa == null ) {
			return new HashSet<>();
		}
		
		return booksJpa.stream()
				.map( e -> fromBookJpaToBookDom(e, genre))
				.collect(Collectors.toSet());	
	}
	
	private Book fromBookJpaToBookDom(BookJpaEntity booksJpa, Genre genre) {
		
		Publisher publisher = fromPublisherJpaToPublisherDom(booksJpa.getPublisher());
				
		Book book = new Book();
		book.setBookId(booksJpa.getBookId());
		book.setIsbn(booksJpa.getIsbn());
		book.setTitle(booksJpa.getTitle());
		book.setCreationTimestamp(booksJpa.getCreationTimestamp());
		book.setUpdateTimestamp(booksJpa.getUpdateTimestamp());
		book.setPublisher(publisher);
		book.setGenre(genre);
	
		return book;
	}	
	
	private Publisher fromPublisherJpaToPublisherDom(PublisherJpaEntity pubJpa) {
		
		Publisher publisher = new Publisher();
		publisher.setPublisherId(pubJpa.getPublisherId());
		publisher.setName(pubJpa.getName());
		publisher.setCreationTimestamp(pubJpa.getCreationTimestamp());
		publisher.setUpdateTimestamp(pubJpa.getUpdateTimestamp());
		publisher.setBooks(new HashSet<>());
		
		return publisher;
	}

	public GenreDto fromDomainToDto(Genre dom) {
		
		return new GenreDto(
				dom.getGenreId(),
				dom.getName(),
				dom.getDescription()
				);
	}
	
	public List<GenreDto> fromListDomainToListDto(List<Genre>list) {

		if ( list == null ) {
			return new ArrayList<>();
		}

		return list.stream()
				.map(this::fromDomainToDto)
				.toList();	
	}	
}

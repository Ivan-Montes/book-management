package dev.ime.common.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import dev.ime.application.dto.AuthorDto;
import dev.ime.application.dto.BookDto;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.Author;
import dev.ime.domain.model.Book;
import dev.ime.domain.model.Genre;
import dev.ime.domain.model.Publisher;
import dev.ime.infrastructure.entity.AuthorJpaEntity;
import dev.ime.infrastructure.entity.BookJpaEntity;
import dev.ime.infrastructure.entity.GenreJpaEntity;
import dev.ime.infrastructure.entity.PublisherJpaEntity;

@Component
public class BookMapper implements GenericMapper<BookDto, Book, BookJpaEntity> {

	private static final Logger logger = LoggerFactory.getLogger(BookMapper.class);

	@Override
	public Book fromDtoToDomain(BookDto dto) {

		Publisher publisher = new Publisher();
		publisher.setPublisherId(dto.publisherId());
		Genre genre = new Genre();
		genre.setGenreId(dto.genreId());
		Set<Author> authors = fromAuthorDtoToAuthorDomain(dto.authors());
				
		Book book = new Book();
		book.setBookId(dto.bookId());
		book.setIsbn(dto.isbn());
		book.setTitle(dto.title());
		book.setPublisher(publisher);
		book.setGenre(genre);
		book.setAuthors(authors);
		
		return book;
	}
	
	private Set<Author> fromAuthorDtoToAuthorDomain(Set<AuthorDto> authors) {

		if ( authors == null ) {
			logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.MSG_COLLECT, GlobalConstants.MSG_EMPTY);
			return new HashSet<>();
		}
		
		return authors.stream()
				.map( item -> {
					Author author = new Author();
					author.setAuthorId(item.authorId());
					author.setName(item.name());
					author.setSurname(item.surname());
					return author;
				})
				.collect(Collectors.toSet());
	}
	
	@Override
	public BookJpaEntity fromDomainToJpa(Book dom) {

		PublisherJpaEntity publisher = new PublisherJpaEntity();
		publisher.setPublisherId(dom.getPublisher().getPublisherId());
		GenreJpaEntity genre = new GenreJpaEntity();
		genre.setGenreId(dom.getGenre().getGenreId());

		return BookJpaEntity.builder().bookId(dom.getBookId()).isbn(dom.getIsbn()).title(dom.getTitle())
				.publisher(publisher).genre(genre).build();
	}

	@Override
	public Book fromJpaToDomain(BookJpaEntity jpa) {
		
		Publisher publisher = fromPublisherJpaToPublisherDomain(jpa.getPublisher());
		Genre genre = fromGenreJpaToGenreDomain(jpa.getGenre());
		Set<Author>authors = fromListAuthorJpaToListAuthorDomain(jpa.getAuthors());
		
		Book book = new Book();
		book.setBookId(jpa.getBookId());
		book.setIsbn(jpa.getIsbn());
		book.setTitle(jpa.getTitle());
		book.setPublisher(publisher);
		book.setGenre(genre);
		book.setAuthors(authors);		
		
		return book;
	}

	private Publisher fromPublisherJpaToPublisherDomain(PublisherJpaEntity jpa) {
		
		Publisher publisher = new Publisher();
		publisher.setPublisherId(jpa.getPublisherId());
		publisher.setName(jpa.getName());
		
		return publisher;
	}

	private Genre fromGenreJpaToGenreDomain(GenreJpaEntity jpa) {
		
		Genre genre = new Genre();
		genre.setGenreId(jpa.getGenreId());
		genre.setName(jpa.getName());
		genre.setDescription(jpa.getDescription());
		
		return genre;
	}
	
	private Set<Author> fromListAuthorJpaToListAuthorDomain(Set<AuthorJpaEntity> list) {

		if ( list == null ) {
			return new HashSet<>();
		}

		return list.stream()
				.map(this::fromAuthorJpaToAuthorDomain)
				.collect(Collectors.toSet());
	}
	
	private Author fromAuthorJpaToAuthorDomain(AuthorJpaEntity jpa) {

		Author author = new Author();
		author.setAuthorId(jpa.getAuthorId());
		author.setName(jpa.getName());
		author.setSurname(jpa.getSurname());
	
		return author;
	}

	@Override
	public List<Book> fromListJpaToListDomain(List<BookJpaEntity> list) {

		if (list == null) {
			return new ArrayList<>();
		}

		return list.stream().map(this::fromJpaToDomain).toList();
	}

	@Override
	public BookDto fromDomainToDto(Book dom) {

		Set<AuthorDto> authorDtoSet = fromSetAuthorDomainToSetAuthorDto(dom.getAuthors());
		Set<Long> authorsIdSet = fromAuthorSetToLongSet(dom.getAuthors());
		return new BookDto(dom.getBookId(), dom.getIsbn(), dom.getTitle(), dom.getPublisher().getPublisherId(),
				dom.getPublisher().getName(),dom.getGenre().getGenreId(),dom.getGenre().getName(), authorsIdSet, authorDtoSet);
	}

	private Set<AuthorDto> fromSetAuthorDomainToSetAuthorDto(Set<Author> list) {

		if ( list == null ) {
			return new HashSet<>();
		}

		return list.stream()
				.map(this::fromAuthorDomainToAuthorDto)
				.collect(Collectors.toSet());	
	}
	
	private AuthorDto fromAuthorDomainToAuthorDto(Author dom) {
		
		return new AuthorDto(
				dom.getAuthorId(),
				dom.getName(),
				dom.getSurname()
				);
	}

	private Set<Long> fromAuthorSetToLongSet(Set<Author> list) {
		
		if ( list == null ) {
			return new HashSet<>();
		}
		
		return list.stream()
				.map(Author::getAuthorId)
				.collect(Collectors.toSet());
	}
	
	@Override
	public List<BookDto> fromListDomainToListDto(List<Book> list) {

		if (list == null) {
			return new ArrayList<>();
		}

		return list.stream().map(this::fromDomainToDto).toList();
	}
}

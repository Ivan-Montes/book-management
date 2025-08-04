package dev.ime.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import dev.ime.dto.AuthorDto;
import dev.ime.entity.AuthorJpaEntity;
import dev.ime.entity.BookJpaEntity;
import dev.ime.model.Author;
import dev.ime.model.Book;
import dev.ime.model.Genre;
import dev.ime.model.Publisher;

@Component
public class AuthorMapper implements GenericMapper<AuthorDto, Author, AuthorJpaEntity> {

	@Override
	public Author fromDtoToDomain(AuthorDto dto) {
		
		Author author = new Author();
		author.setAuthorId(dto.authorId());
		author.setName(dto.name());
		author.setSurname(dto.surname());
		
		return author;
	}

	@Override
	public AuthorJpaEntity fromDomainToJpa(Author dom) {
		
		return AuthorJpaEntity.builder()
				.authorId(dom.getAuthorId())
				.name(dom.getName())
				.surname(dom.getSurname())
				.build();
	}

	@Override
	public Author fromJpaToDomain(AuthorJpaEntity jpa) {

		Author author = new Author();
		author.setAuthorId(jpa.getAuthorId());
		author.setName(jpa.getName());
		author.setSurname(jpa.getSurname());
		
		Set<Book> books = fromSetBookJpaToSetBookDom(jpa.getBooks(), author);
		author.setBooks(books);
		return author;
	}

	private Set<Book> fromSetBookJpaToSetBookDom(Set<BookJpaEntity> booksJpa, Author author) {

		if ( booksJpa == null ) {
			return new HashSet<>();
		}
		
		return booksJpa.stream()
				.map( e -> fromBookJpaToBookDom(e, author))
				.collect(Collectors.toSet());	
	}

	
	private Book fromBookJpaToBookDom(BookJpaEntity booksJpa, Author author) {

		Book book = new Book();
		book.setBookId(booksJpa.getBookId());
		book.setIsbn(booksJpa.getIsbn());
		book.setTitle(booksJpa.getTitle());
		book.setCreationTimestamp(booksJpa.getCreationTimestamp());
		book.setUpdateTimestamp(booksJpa.getUpdateTimestamp());
		book.setPublisher(new Publisher());
		book.setGenre(new Genre());
		book.getAuthors().add(author);
		
		return book;	
	}

	@Override
	public List<Author> fromListJpaToListDomain(List<AuthorJpaEntity> list) {

		if ( list == null ) {
			return new ArrayList<>();
		}

		return list.stream()
				.map(this::fromJpaToDomain)
				.toList();	
	}

	@Override
	public AuthorDto fromDomainToDto(Author dom) {
		
		return new AuthorDto(
				dom.getAuthorId(),
				dom.getName(),
				dom.getSurname()
				);
	}

	@Override
	public List<AuthorDto> fromListDomainToListDto(List<Author> list) {

		if ( list == null ) {
			return new ArrayList<>();
		}

		return list.stream()
				.map(this::fromDomainToDto)
				.toList();	
	}
}

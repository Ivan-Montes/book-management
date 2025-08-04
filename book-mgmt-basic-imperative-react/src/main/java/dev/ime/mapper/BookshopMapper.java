package dev.ime.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import dev.ime.dto.BookshopDto;
import dev.ime.entity.BookBookshopJpaEntity;
import dev.ime.entity.BookshopJpaEntity;
import dev.ime.model.Book;
import dev.ime.model.BookBookshop;
import dev.ime.model.Bookshop;

@Component
public class BookshopMapper implements GenericMapper<BookshopDto, Bookshop, BookshopJpaEntity> {

	@Override
	public Bookshop fromDtoToDomain(BookshopDto dto) {
		
		Bookshop bookshop = new Bookshop();
		bookshop.setBookshopId(dto.bookshopId());
		bookshop.setName(dto.name());
		
		return bookshop;
	}

	@Override
	public BookshopJpaEntity fromDomainToJpa(Bookshop dom) {
		
		return BookshopJpaEntity.builder()
				.bookshopId(dom.getBookshopId())
				.name(dom.getName())
				.build();
	}

	@Override
	public Bookshop fromJpaToDomain(BookshopJpaEntity jpa) {

		Bookshop bookshop = new Bookshop();
		bookshop.setBookshopId(jpa.getBookshopId());
		bookshop.setName(jpa.getName());
		
		Set<BookBookshop> books =  fromSetBookJpaToSetBookDom(jpa.getBooks(), bookshop);
		bookshop.setBooks(books);
		return bookshop;
	}

	private Set<BookBookshop> fromSetBookJpaToSetBookDom(Set<BookBookshopJpaEntity> booksJpa, Bookshop bookshop) {

		if ( booksJpa == null ) {
			return new HashSet<>();
		}
		
		return booksJpa.stream()
				.map( e -> fromBookJpaToBookDom(e, bookshop))
				.collect(Collectors.toSet());	
	}

	private BookBookshop fromBookJpaToBookDom(BookBookshopJpaEntity bookJpa, Bookshop bookshop) {
		
		BookBookshop bookBookshop = new BookBookshop();
		bookBookshop.setBook(new Book());
		bookBookshop.setBookshop(bookshop);
		bookBookshop.setPrice(bookJpa.getPrice());
		bookBookshop.setUnits(bookJpa.getUnits());
		
		return bookBookshop;
	}

	@Override
	public List<Bookshop> fromListJpaToListDomain(List<BookshopJpaEntity> list) {

		if ( list == null ) {
			return new ArrayList<>();
		}

		return list.stream()
				.map(this::fromJpaToDomain)
				.toList();	
	}

	@Override
	public BookshopDto fromDomainToDto(Bookshop dom) {
		
		return new BookshopDto(
				dom.getBookshopId(),
				dom.getName()
				);
	}

	@Override
	public List<BookshopDto> fromListDomainToListDto(List<Bookshop> list) {

		if ( list == null ) {
			return new ArrayList<>();
		}

		return list.stream()
				.map(this::fromDomainToDto)
				.toList();	
	}
}

package dev.ime.common.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.BookBookshopDto;
import dev.ime.domain.model.Book;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.model.Genre;
import dev.ime.domain.model.Publisher;
import dev.ime.infrastructure.entity.BookBookshopId;
import dev.ime.infrastructure.entity.BookBookshopJpaEntity;
import dev.ime.infrastructure.entity.BookJpaEntity;
import dev.ime.infrastructure.entity.BookshopJpaEntity;

@Component
public class BookBookshopMapper implements GenericMapper<BookBookshopDto, BookBookshop, BookBookshopJpaEntity> {

	@Override
	public BookBookshop fromDtoToDomain(BookBookshopDto dto) {
		
		Book book = new Book();
		book.setBookId(dto.bookId());
		Bookshop bookshop = new Bookshop();
		bookshop.setBookshopId(dto.bookshopId());
		
		BookBookshop bookBookshop = new BookBookshop();
		bookBookshop.setBook(book);
		bookBookshop.setBookshop(bookshop);
		bookBookshop.setPrice(dto.price());
		bookBookshop.setUnits(dto.units());
		
		return bookBookshop;
	}

	@Override
	public BookBookshopJpaEntity fromDomainToJpa(BookBookshop dom) {
		
		UUID bookId = dom.getBook().getBookId();
		UUID bookshopId = dom.getBookshop().getBookshopId();		
		BookBookshopId bookBookshopId = new BookBookshopId(bookId,bookshopId);		
		BookJpaEntity book = new BookJpaEntity();
		book.setBookId(bookId);
		BookshopJpaEntity bookshop = new BookshopJpaEntity();
		bookshop.setBookshopId(bookshopId);
		
		return BookBookshopJpaEntity.builder()
				.bookBookshopId(bookBookshopId)
				.book(book)
				.bookshop(bookshop)
				.price(dom.getPrice())
				.units(dom.getUnits())
				.build();
	}

	@Override
	public BookBookshop fromJpaToDomain(BookBookshopJpaEntity jpa) {		

		Book book = fromBookJpaToBookDomain(jpa.getBook());
		Bookshop bookshop = fromBookshopJpaToBookshopDomain(jpa.getBookshop());
		
		BookBookshop bookBookshop = new BookBookshop();
		bookBookshop.setBook(book);
		bookBookshop.setBookshop(bookshop);
		bookBookshop.setPrice(jpa.getPrice());
		bookBookshop.setUnits(jpa.getUnits());
		
		return bookBookshop;
	}

	private Book fromBookJpaToBookDomain(BookJpaEntity jpa) {

		Book book = new Book();
		book.setBookId(jpa.getBookId());
		book.setIsbn(jpa.getIsbn());
		book.setTitle(jpa.getTitle());
		book.setCreationTimestamp(jpa.getCreationTimestamp());
		book.setUpdateTimestamp(jpa.getUpdateTimestamp());
		book.setPublisher(new Publisher());
		book.setGenre(new Genre());
	
		return book;
	}

	private Bookshop fromBookshopJpaToBookshopDomain(BookshopJpaEntity jpa) {

		Bookshop bookshop = new Bookshop();
		bookshop.setBookshopId(jpa.getBookshopId());
		bookshop.setName(jpa.getName());		
		
		return bookshop;
	}
	
	
	@Override
	public List<BookBookshop> fromListJpaToListDomain(List<BookBookshopJpaEntity> list) {

		if ( list == null ) {
			return new ArrayList<>();
		}

		return list.stream()
				.map(this::fromJpaToDomain)
				.toList();	
	}

	@Override
	public BookBookshopDto fromDomainToDto(BookBookshop dom) {
		
		return new BookBookshopDto(
		dom.getBook().getBookId(),
		dom.getBook().getIsbn(),
		dom.getBook().getTitle(),
		dom.getBookshop().getBookshopId(),
		dom.getBookshop().getName(),
		dom.getPrice(),
		dom.getUnits()
		);
	}

	@Override
	public List<BookBookshopDto> fromListDomainToListDto(List<BookBookshop> list) {

		if ( list == null ) {
			return new ArrayList<>();
		}

		return list.stream()
				.map(this::fromDomainToDto)
				.toList();	
	}		
}

package dev.ime.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Book {

	private UUID bookId;	
	private String isbn;
	private String title;
	private LocalDateTime creationTimestamp;
	private LocalDateTime updateTimestamp;
	private Publisher publisher;
	private Genre genre;
	private Set<Author>authors = new HashSet<>();
	private Set<BookBookshop> bookshops = new HashSet<>();
	
	public Book() {
		super();
	}
	
	public Book(UUID bookId, String isbn, String title, LocalDateTime creationTimestamp, LocalDateTime updateTimestamp,
			Publisher publisher, Genre genre, Set<Author> authors, Set<BookBookshop> bookshops) {
		super();
		this.bookId = bookId;
		this.isbn = isbn;
		this.title = title;
		this.creationTimestamp = creationTimestamp;
		this.updateTimestamp = updateTimestamp;
		this.publisher = publisher;
		this.genre = genre;
		this.authors = authors;
		this.bookshops = bookshops;
	}
	
	public UUID getBookId() {
		return bookId;
	}
	public void setBookId(UUID bookId) {
		this.bookId = bookId;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public LocalDateTime getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(LocalDateTime creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	public LocalDateTime getUpdateTimestamp() {
		return updateTimestamp;
	}
	public void setUpdateTimestamp(LocalDateTime updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
	public Publisher getPublisher() {
		return publisher;
	}
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}
	public Genre getGenre() {
		return genre;
	}
	public void setGenre(Genre genre) {
		this.genre = genre;
	}
	public Set<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}
	public Set<BookBookshop> getBookshops() {
		return bookshops;
	}
	public void setBookshops(Set<BookBookshop> bookshops) {
		this.bookshops = bookshops;
	}
	@Override
	public int hashCode() {
		return Objects.hash(bookId, creationTimestamp, genre, isbn, publisher, title, updateTimestamp);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(bookId, other.bookId) && Objects.equals(creationTimestamp, other.creationTimestamp)
				&& Objects.equals(genre, other.genre) && Objects.equals(isbn, other.isbn)
				&& Objects.equals(publisher, other.publisher) && Objects.equals(title, other.title)
				&& Objects.equals(updateTimestamp, other.updateTimestamp);
	}	
}

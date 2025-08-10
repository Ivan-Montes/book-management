package dev.ime.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Bookshop {
	private Long bookshopId;
	private String name;
	private Set<BookBookshop> books = new HashSet<>();
	private LocalDateTime creationTimestamp;
	private LocalDateTime updateTimestamp;
	
	public Bookshop() {
		super();
	}

	public Bookshop(Long bookshopId, String name, Set<BookBookshop> books, LocalDateTime creationTimestamp,
			LocalDateTime updateTimestamp) {
		super();
		this.bookshopId = bookshopId;
		this.name = name;
		this.books = books;
		this.creationTimestamp = creationTimestamp;
		this.updateTimestamp = updateTimestamp;
	}

	public Long getBookshopId() {
		return bookshopId;
	}

	public void setBookshopId(Long bookshopId) {
		this.bookshopId = bookshopId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<BookBookshop> getBooks() {
		return books;
	}

	public void setBooks(Set<BookBookshop> books) {
		this.books = books;
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

	@Override
	public int hashCode() {
		return Objects.hash(bookshopId, creationTimestamp, name, updateTimestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bookshop other = (Bookshop) obj;
		return Objects.equals(bookshopId, other.bookshopId)
				&& Objects.equals(creationTimestamp, other.creationTimestamp) && Objects.equals(name, other.name)
				&& Objects.equals(updateTimestamp, other.updateTimestamp);
	}

	@Override
	public String toString() {
		return "Bookshop [bookshopId=" + bookshopId + ", name=" + name + ", creationTimestamp=" + creationTimestamp
				+ ", updateTimestamp=" + updateTimestamp + "]";
	}		
}

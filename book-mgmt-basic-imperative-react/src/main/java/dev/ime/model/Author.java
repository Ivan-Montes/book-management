package dev.ime.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Author {

	private Long authorId;
	private String name;
	private String surname;
	private LocalDateTime creationTimestamp;
	private LocalDateTime updateTimestamp;
	private Set<Book>books = new HashSet<>();
	
	public Author() {
		super();
	}
	
	public Author(Long authorId, String name, String surname, LocalDateTime creationTimestamp,
			LocalDateTime updateTimestamp, Set<Book> books) {
		super();
		this.authorId = authorId;
		this.name = name;
		this.surname = surname;
		this.creationTimestamp = creationTimestamp;
		this.updateTimestamp = updateTimestamp;
		this.books = books;
	}
	
	public Long getAuthorId() {
		return authorId;
	}
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
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
	public Set<Book> getBooks() {
		return books;
	}	
	public void setBooks(Set<Book> books) {
		this.books = books;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(authorId, creationTimestamp, name, surname, updateTimestamp);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		return Objects.equals(authorId, other.authorId) && Objects.equals(creationTimestamp, other.creationTimestamp)
				&& Objects.equals(name, other.name) && Objects.equals(surname, other.surname)
				&& Objects.equals(updateTimestamp, other.updateTimestamp);
	}
	
	@Override
	public String toString() {
		return "Author [authorId=" + authorId + ", name=" + name + ", surname=" + surname + ", creationTimestamp="
				+ creationTimestamp + ", updateTimestamp=" + updateTimestamp + "]";
	}
}

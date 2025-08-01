package dev.ime.model;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Publisher {
	
	private Long publisherId;
	private String name;
	private LocalDateTime creationTimestamp;
	private LocalDateTime updateTimestamp;
	private Set<Book>books = new HashSet<>();
	
	public Publisher() {
		super();
	}
	
	public Publisher(Long publisherId, String name, LocalDateTime creationTimestamp, LocalDateTime updateTimestamp,
			Set<Book> books) {
		super();
		this.publisherId = publisherId;
		this.name = name;
		this.creationTimestamp = creationTimestamp;
		this.updateTimestamp = updateTimestamp;
		this.books = books;
	}
	
	public Long getPublisherId() {
		return publisherId;
	}
	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		return Objects.hash(creationTimestamp, name, publisherId, updateTimestamp);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Publisher other = (Publisher) obj;
		return Objects.equals(creationTimestamp, other.creationTimestamp) && Objects.equals(name, other.name)
				&& Objects.equals(publisherId, other.publisherId)
				&& Objects.equals(updateTimestamp, other.updateTimestamp);
	}
	@Override
	public String toString() {
		return "Publisher [publisherId=" + publisherId + ", name=" + name + ", creationTimestamp=" + creationTimestamp
				+ ", updateTimestamp=" + updateTimestamp + "]";
	}		
	
}

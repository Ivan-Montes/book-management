package dev.ime.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Genre {

	private UUID genreId;
	private String name;
	private String description;
	private LocalDateTime creationTimestamp;
	private LocalDateTime updateTimestamp;
	private Set<Book> books = new HashSet<>();

	public Genre() {
		super();
	}

	public Genre(UUID genreId, String name, String description, LocalDateTime creationTimestamp,
			LocalDateTime updateTimestamp, Set<Book> books) {
		super();
		this.genreId = genreId;
		this.name = name;
		this.description = description;
		this.creationTimestamp = creationTimestamp;
		this.updateTimestamp = updateTimestamp;
		this.books = books;
	}

	public UUID getGenreId() {
		return genreId;
	}

	public void setGenreId(UUID genreId) {
		this.genreId = genreId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		return Objects.hash(creationTimestamp, description, genreId, name, updateTimestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Genre other = (Genre) obj;
		return Objects.equals(creationTimestamp, other.creationTimestamp)
				&& Objects.equals(description, other.description) && Objects.equals(genreId, other.genreId)
				&& Objects.equals(name, other.name) && Objects.equals(updateTimestamp, other.updateTimestamp);
	}

	@Override
	public String toString() {
		return "Genre [genreId=" + genreId + ", name=" + name + ", description=" + description + ", creationTimestamp="
				+ creationTimestamp + ", updateTimestamp=" + updateTimestamp + "]";
	}

}

package dev.ime.dto;

import java.util.Set;

import dev.ime.common.RegexPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookDto(
		Long bookId,
		@Size(min=10, max=13, message="{Size.BookNewDTO.isbn}")
		@Pattern( regexp = "[\\d]{10,13}", message="{Pattern.BookNewDTO.isbn}")
		String isbn,
		@NotBlank
		@Size(min=1, max=100, message="{Size.BookNewDTO.title}")
		@Pattern(regexp = RegexPattern.TITLE_BASIC, message="{Pattern.BookNewDTO.title}")
		String title,		
		@NotNull(message="{NotNull.BookNewDTO.publisherId}")
		Long publisherId,	
		String publisherName,
		@NotNull(message="{NotNull.BookNewDTO.genreId}")
		Long genreId,		
		String genreName,
		@NotNull(message="{NotNull.BookNewDTO.authorId}")
		Set<AuthorDto> authorsSet
		) {

}

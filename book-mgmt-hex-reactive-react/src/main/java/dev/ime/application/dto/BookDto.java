package dev.ime.application.dto;

import java.util.Set;
import java.util.UUID;

import dev.ime.common.constants.GlobalConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookDto(
		UUID bookId,
		@Size(min=10, max=13)
		@Pattern( regexp = GlobalConstants.PATTERN_ISBN)
		String isbn,
		@NotBlank
		@Size(min=1, max=100)
		@Pattern(regexp = GlobalConstants.PATTERN_TITLE_FULL)
		String title,		
		@NotNull
		UUID publisherId,	
		String publisherName,
		@NotNull
		UUID genreId,		
		String genreName,
		@NotNull
		Set<UUID> authorsId,
		Set<AuthorDto> authors
		) {
	public BookDto {
		if (title != null) {
			title = title.trim();
        }		
	}
}

package dev.ime.dto;

import dev.ime.common.RegexPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GenreDto(
		Long genreId, 
		@NotBlank
		@Size( min = 1, max = 50, message="{Size.GenreDTO.name}")
		@Pattern( regexp = RegexPattern.NAME_BASIC, message="{Pattern.GenreDTO.name}")
		String name, 
		@NotBlank
		@Size( min = 1, max = 100, message="{Size.GenreDTO.description}")
		@Pattern( regexp = RegexPattern.DESCRIPTION_BASIC, message="{Pattern.GenreCreationDTO.description}")
		String description) {
}

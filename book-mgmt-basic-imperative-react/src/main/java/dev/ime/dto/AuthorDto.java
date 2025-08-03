package dev.ime.dto;

import dev.ime.common.RegexPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthorDto(
		Long authorId,
		@NotBlank
		@Size(min = 1, max = 50, message="{Size.AuthorDTO.name}")
		@Pattern( regexp = RegexPattern.NAME_BASIC, message="{Pattern.AuthorDTO.name}")
		String name,
		@NotBlank
		@Size(min = 1, max = 50, message="{Size.AuthorDTO.surname}")
		@Pattern( regexp = RegexPattern.SURNAME_BASIC, message="{Pattern.AuthorDTO.surname}")
		String surname
		) {

}

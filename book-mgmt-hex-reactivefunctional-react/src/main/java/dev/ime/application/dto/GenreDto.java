package dev.ime.application.dto;

import java.util.UUID;

import dev.ime.common.constants.GlobalConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GenreDto(
		UUID genreId, 
		@NotBlank
		@Size( min = 1, max = 50)
		@Pattern( regexp = GlobalConstants.PATTERN_NAME_FULL)
		String name, 
		@NotBlank
		@Size( min = 1, max = 100)
		@Pattern( regexp = GlobalConstants.PATTERN_DESC_FULL)
		String description) {
	public GenreDto {
        if (name != null) {
            name = name.trim();
        }
        if (description != null) {
        	description = description.trim();
        }
	}
}

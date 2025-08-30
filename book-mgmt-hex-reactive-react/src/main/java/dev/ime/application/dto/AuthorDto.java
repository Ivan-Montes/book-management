package dev.ime.application.dto;

import java.util.UUID;

import dev.ime.common.constants.GlobalConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthorDto(
		UUID authorId,
		@NotBlank
		@Size(min = 1, max = 50)
		@Pattern( regexp = GlobalConstants.PATTERN_NAME_FULL)
		String name,
		@NotBlank
		@Size(min = 1, max = 50)
		@Pattern( regexp = GlobalConstants.PATTERN_SURNAME_FULL)
		String surname
		) {
	public AuthorDto {
        if (name != null) {
            name = name.trim();
        }
        if (surname != null) {
            surname = surname.trim();
        }
	}
}

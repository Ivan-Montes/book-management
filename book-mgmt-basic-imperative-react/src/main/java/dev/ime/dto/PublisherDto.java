package dev.ime.dto;

import dev.ime.common.RegexPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PublisherDto(
		Long publisherId,
		@NotBlank
		@Size( min = 1, max = 50, message="{Size.PublisherDTO.name}")
		@Pattern(regexp = RegexPattern.NAME_BASIC, message="{Pattern.PublisherDTO.name}")
		String name
		) {
}

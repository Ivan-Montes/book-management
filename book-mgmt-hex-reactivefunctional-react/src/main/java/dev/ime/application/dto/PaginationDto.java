package dev.ime.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PaginationDto(
	    @Min(0) Integer page,
	    @Min(1)@Max(100)Integer size,
	    @NotBlank String sortBy,
	    @NotBlank String sortDir
	    ) {

}

package dev.ime.application.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookBookshopDto(
		@NotNull
		UUID bookId,
		@Size(min=10, max=13)
		String isbn,
		@Size(min=1, max=100)
		String title,		
		@NotNull
		UUID bookshopId,
		@Size( min = 1, max = 50)
		String name,		
		@NotNull
		@Max(value=999)
		@Min(value=0)
		Double price,		
		@NotNull
		@Max(value=99)
		@Min(value=0)
		Integer units
		) {
}

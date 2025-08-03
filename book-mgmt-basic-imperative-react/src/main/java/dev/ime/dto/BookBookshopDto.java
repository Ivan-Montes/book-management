package dev.ime.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookBookshopDto(
		@NotNull(message="{NotNull.BookBookshopDTO.bookId}")
		Long bookId,
		@Size(min=10, max=13, message="{Size.BookBookshopDTO.isbn}")
		String isbn,
		@Size(min=1, max=100, message="{Size.BookBookshopDTO.title}")
		String title,		
		@NotNull(message="{NotNull.BookBookshopDTO.bookshopId}")
		Long bookshopId,
		@Size( min = 1, max = 50,  message="{Size.BookBookshopDTO.name}")
		String name,		
		@NotNull
		@Max(value=999, message = "{Max.BookBookshopDTO.price}")
		@Min(value=0, message = "{Min.BookBookshopDTO.price}")
		Double price,		
		@NotNull
		@Max(value=99, message = "{Max.BookBookshopDTO.units}")
		@Min(value=0, message = "{Min.BookBookshopDTO.units}")
		Integer units
		) {

}

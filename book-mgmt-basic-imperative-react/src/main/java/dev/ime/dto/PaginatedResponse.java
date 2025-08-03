package dev.ime.dto;

import java.util.List;

import dev.ime.model.PaginatedResult;

public record PaginatedResponse<T>(
	    List<T> content,
	    int page,
	    int size,
	    long totalElements,
	    int totalPages,
	    boolean last) implements PaginatedResult<T>  {
}

package dev.ime.application.dto;

import java.util.List;

import dev.ime.domain.model.PaginatedResult;

public record PaginatedDto<T>(
	    List<T> content,
	    int page,
	    int size,
	    long totalElements,
	    int totalPages,
	    boolean last) implements PaginatedResult<T>  {
}

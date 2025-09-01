package dev.ime.domain.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import dev.ime.application.dto.PaginatedDto;

@JsonSerialize(as = PaginatedDto.class)
public interface PaginatedResult<T> {
    List<T> content();
    int page();
    int size();
    long totalElements();
    int totalPages();
    boolean last();
}
